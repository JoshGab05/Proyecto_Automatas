package com.pythonlex.gui;

import com.pythonlex.core.*;
import com.pythonlex.lexer.PythonLexerWrapper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.io.File;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainWindow extends JFrame {
    private final JTextPane output = new JTextPane();
    private final JLabel status = new JLabel("Listo");
    private final TokenStatsPanel statsPanel = new TokenStatsPanel();

    // Últimos issues calculados por el validador (todos los errores)
    private List<PostLexValidator.Issue> lastIssues = List.of();

    public MainWindow() {
        super("PythonLex GUI");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1150, 720);
        setLocationRelativeTo(null);

        // ===== Menú =====
        JMenuBar mb = new JMenuBar();
        JMenu menu = new JMenu("Archivo");
        JMenuItem abrir = new JMenuItem("Abrir…");
        abrir.addActionListener(e -> openAndAnalyze());
        JMenuItem salir = new JMenuItem("Salir");
        salir.addActionListener(e -> System.exit(0));
        menu.add(abrir);
        menu.addSeparator();
        menu.add(salir);
        mb.add(menu);
        setJMenuBar(mb);

        // ===== Editor (fondo negro) =====
        output.setEditable(false);
        output.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 16));
        output.setBackground(Color.BLACK);
        output.setForeground(new Color(0xE6, 0xE6, 0xE6));
        output.setCaretColor(Color.WHITE);

        // Scroll con números de línea en el row header
        JScrollPane editorScroll = new JScrollPane(output);
        editorScroll.setRowHeaderView(new LineNumberView(output));

        // Split: código | estadísticas
        JSplitPane split = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                editorScroll,
                statsPanel
        );
        split.setResizeWeight(1.0); // prioriza el panel izquierdo
        add(split, BorderLayout.CENTER);

        // Barra de estado
        JPanel south = new JPanel(new BorderLayout());
        south.setBorder(new EmptyBorder(6, 8, 6, 8));
        status.setFont(status.getFont().deriveFont(Font.BOLD, 13f));
        south.add(status, BorderLayout.WEST);
        add(south, BorderLayout.SOUTH);

        // Stats vacías
        statsPanel.updateCounts(TokenStatsPanel.emptyCounts(), 0);
    }

    private void openAndAnalyze() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Selecciona archivo .py");
        int r = fc.showOpenDialog(this);
        if (r != JFileChooser.APPROVE_OPTION) return;
        analyzeFile(fc.getSelectedFile());
    }

    private void analyzeFile(File file) {
        try {
            PythonLexerWrapper wrapper = new PythonLexerWrapper();
            AnalysisResult raw = wrapper.analyze(file);   // ahora recolecta TODOS los tokens (no se detiene)

            // Valida y recolecta TODOS los problemas
            lastIssues = PostLexValidator.validateAll(raw.getTokens());

            renderAnalysis(raw);      // pinta todo el archivo, resaltando issues
            updateStats(raw);         // estadísticas del archivo completo

            if (lastIssues.isEmpty()) {
                status.setForeground(new Color(0x27, 0xC4, 0x6C)); // verde
                status.setText("✅ VÁLIDO — " + file.getName());
            } else {
                var first = lastIssues.get(0);
                status.setForeground(new Color(0xFF, 0x55, 0x55)); // rojo
                status.setText("❌ " + lastIssues.size() + " error(es). Primero: " + first.message);
            }
        } catch (Exception ex) {
            status.setForeground(new Color(0xFF, 0x55, 0x55));
            status.setText("Fallo al analizar: " + ex.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Fallo al analizar:\n" + ex,
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Pinta todos los tokens. Si hay issues, resalta en rojo el token implicado o inserta el “esperado”. */
    private void renderAnalysis(AnalysisResult result) throws BadLocationException {
        StyledDocument doc = output.getStyledDocument();
        doc.remove(0, doc.getLength());

        // Índices de tokens que deben ir en ERROR (y, si aplica, lexema “esperado” a insertar)
        Map<Integer, String> errorAtIndex = new HashMap<>();
        for (PostLexValidator.Issue is : lastIssues) {
            // replaceStyleOnly == true => resaltar el token existente
            // replaceStyleOnly == false => insertar marcador del esperado (":", ")")
            errorAtIndex.put(is.index, is.replaceStyleOnly ? null : (is.expectedLexeme == null ? "" : is.expectedLexeme));
        }

        var toks = result.getTokens();
        for (int i = 0; i < toks.size(); i++) {
            Token t = toks.get(i);
            TokenType type = t.getType();
            String lex = t.getLexeme();
            if (lex == null) lex = "";

            // NEWLINE / WHITESPACE se insertan tal cual
            if (type == TokenType.NEWLINE) { doc.insertString(doc.getLength(), "\n", null); continue; }
            if (type == TokenType.WHITESPACE) { doc.insertString(doc.getLength(), lex, null); continue; }

            // ¿Este índice tiene issue?
            if (errorAtIndex.containsKey(i)) {
                SimpleAttributeSet err = new SimpleAttributeSet();
                Color fg = TokenStyles.fg(TokenType.ERROR);
                Color bg = TokenStyles.bg(TokenType.ERROR);
                if (fg != null) StyleConstants.setForeground(err, fg);
                if (bg != null) StyleConstants.setBackground(err, bg);

                String expected = errorAtIndex.get(i);
                if (expected == null) {
                    // Resalta el token existente como error
                    doc.insertString(doc.getLength(), lex, err);
                } else {
                    // Inserta marcador del token esperado (p.ej. ":" o ")") en rojo.
                    doc.insertString(doc.getLength(), expected, err);
                }
                continue; // seguir con el resto (no detenemos)
            }

            // Normal
            SimpleAttributeSet attrs = new SimpleAttributeSet();
            Color fg = TokenStyles.fg(type);
            Color bg = TokenStyles.bg(type);
            if (fg != null) StyleConstants.setForeground(attrs, fg);
            if (bg != null) StyleConstants.setBackground(attrs, bg);
            doc.insertString(doc.getLength(), lex, attrs);
        }

        output.setCaretPosition(0);
    }

    /** Calcula y muestra el conteo por tipo (sobre todos los tokens). */
    private void updateStats(AnalysisResult result) {
        EnumMap<TokenType, Integer> counts = new EnumMap<>(TokenType.class);
        for (TokenType t : TokenType.values()) counts.put(t, 0);

        for (Token t : result.getTokens()) {
            counts.put(t.getType(), counts.get(t.getType()) + 1);
        }
        statsPanel.updateCounts(counts, result.getTokens().size());
    }
}
