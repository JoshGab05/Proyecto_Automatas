package com.pythonlex.gui;

import javax.swing.*;
import javax.swing.text.Element;
import java.awt.*;

public class LineNumberView extends JComponent {
    private final JTextPane text;
    private final FontMetrics fm;
    private final int inset = 6;

    public LineNumberView(JTextPane text) {
        this.text = text;
        Font f = new Font(Font.MONOSPACED, Font.PLAIN, 14);
        setFont(f);
        fm = getFontMetrics(f);
        setForeground(new Color(0x88, 0x88, 0x88));
        setBackground(new Color(0x12, 0x12, 0x12));
        setOpaque(true);

        text.getDocument().addDocumentListener((SimpleDocumentListener) e -> repaint());
        text.addCaretListener(e -> repaint());
    }

    @Override public Dimension getPreferredSize() {
        int lines = getLineCount();
        int digits = Math.max(3, String.valueOf(lines).length());
        int w = inset*2 + digits * fm.charWidth('0');
        return new Dimension(w, text.getHeight());
    }

    @Override protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Rectangle clip = g.getClipBounds();
        g.setColor(getBackground());
        g.fillRect(clip.x, clip.y, clip.width, clip.height);

        int start = text.viewToModel2D(new Point(0, clip.y));
        int end   = text.viewToModel2D(new Point(0, clip.y + clip.height));

        Element root = text.getDocument().getDefaultRootElement();
        int startLine = root.getElementIndex(start);
        int endLine   = root.getElementIndex(end);

        for (int line = startLine; line <= endLine; line++) {
            try {
                Rectangle r = text.modelToView2D(root.getElement(line).getStartOffset()).getBounds();
                String s = String.valueOf(line + 1);
                g.setColor(getForeground());
                int x = getPreferredSize().width - inset - fm.stringWidth(s);
                int y = r.y + r.height - fm.getDescent();
                g.drawString(s, x, y);
            } catch (Exception ignored) {}
        }
    }

    private int getLineCount() {
        return text.getDocument().getDefaultRootElement().getElementCount();
    }

    @FunctionalInterface
    interface SimpleDocumentListener extends javax.swing.event.DocumentListener {
        void update(javax.swing.event.DocumentEvent e);
        default void insertUpdate(javax.swing.event.DocumentEvent e){update(e);}
        default void removeUpdate(javax.swing.event.DocumentEvent e){update(e);}
        default void changedUpdate(javax.swing.event.DocumentEvent e){update(e);}
    }
}
