package com.pythonlex.gui;

import com.pythonlex.core.TokenType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.EnumMap;
import java.util.Map;

public class TokenStatsPanel extends JPanel {
    private final DefaultTableModel model;

    public TokenStatsPanel() {
        super(new BorderLayout());
        model = new DefaultTableModel(new Object[]{"Tipo", "Cantidad"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        table.setRowHeight(22);
        add(new JScrollPane(table), BorderLayout.CENTER);
        setPreferredSize(new Dimension(260, 0));
    }

    public void updateCounts(Map<TokenType, Integer> counts, int total) {
        model.setRowCount(0);
        // Muestra en un orden agradable
        TokenType[] order = {
                TokenType.KEYWORD, TokenType.IDENT, TokenType.NUMBER, TokenType.STRING,
                TokenType.COMMENT, TokenType.OP, TokenType.GROUP, TokenType.NEWLINE,
                TokenType.WHITESPACE, TokenType.ERROR
        };
        for (TokenType t : order) {
            Integer n = counts.getOrDefault(t, 0);
            model.addRow(new Object[]{t.name(), n});
        }
        model.addRow(new Object[]{"TOTAL", total});
    }

    public static Map<TokenType, Integer> emptyCounts() {
        EnumMap<TokenType, Integer> m = new EnumMap<>(TokenType.class);
        for (TokenType t : TokenType.values()) m.put(t, 0);
        return m;
    }
}
