package com.pythonlex.gui;

import com.pythonlex.core.*;
import com.pythonlex.engine.PythonAnalyzer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class AppFrame extends JFrame {

    private final JTextPane codePane = new JTextPane();
    private final JTable tokenTable;
    private final JLabel status = new JLabel("🌙 GUI DARK – listo.");
    private final Analyzer analyzer = new PythonAnalyzer();

    private Style styleKeyword, styleNumber, styleString, styleComment, styleDefault, styleErrorUnderline;

    public AppFrame() {
        super("PythonLex – GUI v2 (DARK)"); // <—— título distinto
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);

        // Tema oscuro explícito
        Color bg = new Color(0x1e1e1e);
        Color fg = new Color(0xdddddd);
        codePane.setBackground(bg);
        codePane.setForeground(fg);
        codePane.setCaretColor(fg);
        codePane.setFont(new Font("Consolas", Font.PLAIN, 16));

        configureStyles(); // colores por tipo de token

        // Tabla para tokens (también oscura)
        String[] columns = {"Tipo", "Lexema", "Línea", "Columna"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        tokenTable = new JTable(model);
        tokenTable.setFont(new Font("Consolas", Font.PLAIN, 13));
        tokenTable.setRowHeight(22);
        tokenTable.setEnabled(false);
        tokenTable.setBackground(new Color(0x252526));
        tokenTable.setForeground(new Color(0xd0d0d0));
        tokenTable.setGridColor(new Color(0x3c3c3c));
        tokenTable.getTableHeader().setBackground(new Color(0x333333));
        tokenTable.getTableHeader().setForeground(new Color(0xeeeeee));

        // Panel superior (botones)
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topBar.setBorder(new EmptyBorder(5, 5, 5, 5));
        topBar.setBackground(new Color(0x2d2d30));
        JButton bOpen = new JButton("📂 Abrir .py");
        JButton bAnalyze = new JButton("🔍 Analizar");
        JButton bExit = new JButton("❌ Salir");
        bOpen.addActionListener(this::onOpen);
        bAnalyze.addActionListener(this::onAnalyze);
        bExit.addActionListener(e -> dispose());
        topBar.add(bOpen); topBar.add(bAnalyze); topBar.add(bExit);

        // División vertical (código arriba, tabla abajo)
        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(codePane),
                new JScrollPane(tokenTable)
        );
        split.setResizeWeight(0.7);
        split.setOneTouchExpandable(true);
        split.setDividerLocation(0.65); // <—— mueve el divisor
        split.setBackground(new Color(0x2d2d30));

        // Layout principal
        add(topBar, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);

        // Barra inferior de estado
        status.setBorder(new EmptyBorder(5, 10, 5, 10));
        status.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        status.setForeground(new Color(0xeeeeee));
        status.setBackground(new Color(0x2d2d30));
        status.setOpaque(true);
        add(status, BorderLayout.SOUTH);
    }

    private void configureStyles() {
        StyledDocument doc = codePane.getStyledDocument();

        styleDefault = doc.addStyle("default", null);
        StyleConstants.setForeground(styleDefault, new Color(0xdddddd)); // texto principal claro

        styleKeyword = doc.addStyle("kw", null);
        StyleConstants.setForeground(styleKeyword, new Color(0x4FC1FF)); // Azul brillante

        styleNumber = doc.addStyle("num", null);
        StyleConstants.setForeground(styleNumber, new Color(0xDBA800)); // Naranja cálido

        styleString = doc.addStyle("str", null);
        StyleConstants.setForeground(styleString, new Color(0xCE9178)); // Rojo suave

        styleComment = doc.addStyle("com", null);
        StyleConstants.setForeground(styleComment, new Color(0x6A9955)); // Verde tipo VSCode

        styleErrorUnderline = doc.addStyle("err", null);
        StyleConstants.setUnderline(styleErrorUnderline, true);
        StyleConstants.setForeground(styleErrorUnderline, new Color(0xFF5555));
    }

    private void onOpen(ActionEvent e) {
        JFileChooser fc = new JFileChooser();
        int r = fc.showOpenDialog(this);
        if (r == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            try {
                String text = Files.readString(f.toPath());
                codePane.setText(text);
                status.setText("📄 Archivo cargado: " + f.getName());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error leyendo archivo: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearStyles() {
        StyledDocument doc = codePane.getStyledDocument();
        doc.setCharacterAttributes(0, doc.getLength(), styleDefault, true);
    }

    private void onAnalyze(ActionEvent e) {
        try {
            clearStyles();
            String text = codePane.getText();
            AnalysisResult res = analyzer.analyzeString(text);
            colorize(res);
            fillTable(res);
            
            if (res.firstError != null) {
                status.setText(String.format("❌ ERROR en línea %d, columna %d",
                        res.firstError.line, res.firstError.column));
            } else {
                status.setText("✅ Análisis completado: válido.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error en análisis: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void fillTable(AnalysisResult res) {
        DefaultTableModel model = (DefaultTableModel) tokenTable.getModel();
        model.setRowCount(0);
        for (Token t : res.tokens) {
            if (t.type == TokenType.EOF) continue;
            model.addRow(new Object[]{ t.type.name(), t.lexeme, t.line, t.column });
        }
    }

    private void colorize(AnalysisResult res) throws BadLocationException {
        StyledDocument doc = codePane.getStyledDocument();
        String full = codePane.getText();
        int[] lineOffsets = buildLineOffsets(full);

        for (Token t : res.tokens) {
            if (t.type == TokenType.EOF) continue;
            int start = toOffset(t.line, t.column, lineOffsets);
            int len = Math.max(t.length, 0);
            if (start < 0 || start + len > doc.getLength()) continue;

    Style s = switch (t.type) {
        case KEYWORD -> styleKeyword;
        case NUMBER  -> styleNumber;
        case STRING  -> styleString;
        case COMMENT -> styleComment;
        default      -> null; // no colorear otros; quedan en negro
};
if (s != null) {
    doc.setCharacterAttributes(start, len, s, true);
}

        }

        if (res.firstError != null) {
            Token e = res.firstError;
            int start = toOffset(e.line, e.column, lineOffsets);
            int len = Math.max(e.length, 1);
            if (start >= 0 && start + len <= doc.getLength()) {
                doc.setCharacterAttributes(start, len, styleErrorUnderline, false);
            }
        }
    }

    private int[] buildLineOffsets(String text) {
        java.util.List<Integer> list = new java.util.ArrayList<>();
        list.add(0);
        for (int i = 0; i < text.length(); i++)
            if (text.charAt(i) == '\n') list.add(i + 1);
        list.add(text.length());
        return list.stream().mapToInt(Integer::intValue).toArray();
    }

    private int toOffset(int line, int col, int[] offs) {
        int idx = Math.max(1, Math.min(line, offs.length - 1)) - 1;
        return offs[idx] + Math.max(0, col - 1);
    }
}
