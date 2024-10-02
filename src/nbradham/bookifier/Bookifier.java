package nbradham.bookifier;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Handles entire program.
 * 
 * @author Nickolas S. Bradham
 */
final class Bookifier {

	/**
	 * Handles everything.
	 * 
	 * @param args Ignored.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			JFrame frame = new JFrame("MC Bookifier");
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLayout(new BorderLayout());
			JPanel head = new JPanel(new GridBagLayout()), foot = new JPanel(new GridLayout(0, 1));
			JTextField authField = new JTextField(), titleField = new JTextField(), com = new JTextField();
			JTextArea text = new JTextArea(14, 16), pages = new JTextArea(14, 16);
			DocumentListener da = new DocumentListener() {
				private static final byte B_W6 = 6;
				private static final HashMap<Character, Byte> HM_WIDTHS = new HashMap<>();
				static {
					char[][] chars = { { '\n' }, {}, { '!', '\'', ',', '.', ':', ';', 'i', '|' }, { '`', 'l' },
							{ '"', '(', ')', '*', 'I', '[', ']', 't', '{', '}', ' ' }, { '<', '>', 'f', 'k' },
							{ '#', '$', '%', '&', '+', '-', '/', '=', '?', '\\', '^', '_' }, { '@', '~' } };
					for (char c = '0'; c <= '9'; ++c)
						HM_WIDTHS.put(c, B_W6);
					for (char c = 'A'; c <= 'Z'; ++c) {
						HM_WIDTHS.put(c, B_W6);
						HM_WIDTHS.put((char) (c + 32), B_W6);
					}
					for (byte x = 0; x < chars.length; ++x)
						for (byte y = 0; y < chars[x].length; ++y)
							HM_WIDTHS.put(chars[x][y], x);
				}

				private final void update() {
					ArrayList<String> pageStrs = new ArrayList<>();
					StringBuilder sb = new StringBuilder();
					byte lw = 0, l = 0;
					for (char c : text.getText().toCharArray()) {
						byte w = HM_WIDTHS.get(c);
						if ((lw += w) > 114 && !Character.isWhitespace(c)) {
							++l;
							int back = sb.length();
							char test;
							lw = w;
							while (!Character.isWhitespace(test = sb.charAt(--back)))
								lw += HM_WIDTHS.get(test);
						}
						if (l > 13) {
							int back = sb.length();
							while (!Character.isWhitespace(sb.charAt(--back)))
								;
							pageStrs.add(sb.substring(0, back));
							sb.delete(0, back + 1);
							l = 0;
							lw += w;
						}
						if (c == '\n') {
							++l;
							lw = 0;
						}
						sb.append(c);
					}
					pageStrs.add(sb.toString());
					pages.setText("");
					l = 0;
					sb.setLength(0);
					sb.append("/give @p minecraft:written_book[minecraft:written_book_content={author:'")
							.append(authField.getText()).append("',title:'").append(titleField.getText())
							.append("',pages:[");
					for (String s : pageStrs) {
						pages.append(String.format("#### Page %3d ####%n%s%n%n", ++l, s));
						sb.append("'[[\"").append(
								s.replaceAll("'", "\\\\'").replaceAll("\n", "\\\\\\\\n").replaceAll("\"", "\\\\\\\\\""))
								.append("\"]]',");
					}
					sb.setLength(sb.length() - 1);
					com.setText(sb.append("]}]").toString());
				}

				@Override
				public final void insertUpdate(DocumentEvent e) {
					update();
				}

				@Override
				public final void removeUpdate(DocumentEvent e) {
					update();
				}

				@Override
				public final void changedUpdate(DocumentEvent e) {
				}
			};
			authField.getDocument().addDocumentListener(da);
			titleField.getDocument().addDocumentListener(da);
			text.getDocument().addDocumentListener(da);
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.gridx = 0;
			gbc.gridy = 0;
			head.add(new JLabel("Author:"), gbc);
			gbc.gridy = 1;
			head.add(new JLabel("Title:"), gbc);
			gbc.gridx = 1;
			gbc.gridy = 0;
			gbc.weightx = 1;
			gbc.fill = GridBagConstraints.HORIZONTAL;
			head.add(authField, gbc);
			gbc.gridy = 1;
			head.add(titleField, gbc);
			frame.add(head, BorderLayout.PAGE_START);
			text.setLineWrap(true);
			pages.setLineWrap(true);
			JScrollPane scroll = new JScrollPane(text), scrollPages = new JScrollPane(pages);
			scroll.setBorder(new TitledBorder("Book Text"));
			scrollPages.setBorder(new TitledBorder("Pages"));
			frame.add(scroll, BorderLayout.CENTER);
			com.setEditable(false);
			com.setBorder(new TitledBorder("Give Command"));
			foot.add(com);
			frame.add(foot, BorderLayout.PAGE_END);
			frame.add(scrollPages, BorderLayout.EAST);
			frame.pack();
			frame.setVisible(true);
		});
	}
}