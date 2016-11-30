package net.filebot.ui.subtitle;

import static java.awt.Font.*;
import static java.util.Collections.*;
import static java.util.regex.Pattern.*;
import static net.filebot.util.ui.SwingUI.*;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Pattern;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.MouseInputAdapter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import net.filebot.ResourceManager;
import net.filebot.subtitle.SubtitleElement;
import net.filebot.util.ui.GradientStyle;
import net.filebot.util.ui.LazyDocumentListener;
import net.filebot.util.ui.notification.SeparatorBorder;
import net.filebot.util.ui.notification.SeparatorBorder.Position;
import net.miginfocom.swing.MigLayout;

public class SubtitleViewer extends JFrame {

	private final JLabel titleLabel = new JLabel();
	private final JLabel infoLabel = new JLabel();

	private final SubtitleTableModel model = new SubtitleTableModel();
	private final JTable subtitleTable = createTable(model);

	private final JTextField filterEditor = createFilterEditor();

	private Color defaultFilterForeground = filterEditor.getForeground();
	private Color disabledFilterForeground = Color.lightGray;

	public SubtitleViewer(String title) {
		super(title);

		// bold title label in header
		titleLabel.setText(title);
		titleLabel.setFont(titleLabel.getFont().deriveFont(BOLD));

		JPanel header = new JPanel(new MigLayout("insets dialog, nogrid, fillx"));

		header.setBackground(Color.white);
		header.setBorder(new SeparatorBorder(1, new Color(0xB4B4B4), new Color(0xACACAC), GradientStyle.LEFT_TO_RIGHT, Position.BOTTOM));

		header.add(titleLabel, "wrap");
		header.add(infoLabel, "gap indent*2, wrap paragraph:push");

		JPanel content = new JPanel(new MigLayout("fill, insets dialog, nogrid", "[fill]", "[pref!][fill]"));

		content.add(new JLabel("Filter:"), "gap indent:push");
		content.add(filterEditor, "wmin 120px, gap rel");
		content.add(createImageButton(clearFilterAction), "w pref!, h pref!, wrap");
		content.add(new JScrollPane(subtitleTable), "grow");

		JComponent pane = (JComponent) getContentPane();
		pane.setLayout(new MigLayout("fill, insets 0 0 rel 0"));

		pane.add(header, "hmin 20px, growx, dock north");
		pane.add(content, "grow");

		// initialize window properties
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setLocationByPlatform(true);
		setResizable(true);
		pack();
	}

	private JTable createTable(TableModel model) {
		final JTable table = new JTable(model);
		table.setBackground(Color.white);
		table.setAutoCreateRowSorter(true);
		table.setFillsViewportHeight(true);
		table.setRowHeight(18);

		// decrease column width for the row number columns
		DefaultTableColumnModel m = ((DefaultTableColumnModel) table.getColumnModel());
		m.getColumn(0).setMaxWidth(40);
		m.getColumn(1).setMaxWidth(60);
		m.getColumn(2).setMaxWidth(60);

		// initialize selection modes
		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		final DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.ROOT);
		timeFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

		// change time stamp format
		table.setDefaultRenderer(Date.class, new DefaultTableCellRenderer() {

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				return super.getTableCellRendererComponent(table, timeFormat.format(value), isSelected, hasFocus, row, column);
			}
		});

		// change text format
		table.setDefaultRenderer(String.class, new DefaultTableCellRenderer() {

			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				return super.getTableCellRendererComponent(table, value.toString().replaceAll("\\s+", " "), isSelected, hasFocus, row, column);
			}
		});

		// focus around selected time stamp
		installAction(table, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), new AbstractAction("focus") {

			@Override
			public void actionPerformed(ActionEvent e) {
				// disable row filter
				setTableFilter(null);

				// ensure selected row is visible and roughly in the center of the table
				Rectangle focus = table.getCellRect(Math.max(table.getSelectedRow() - 7, 0), 0, true);
				focus.height = table.getSize().height;
				table.scrollRectToVisible(focus);
			}
		});

		table.addMouseListener(new MouseInputAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
					table.getActionMap().get("focus").actionPerformed(null);
				}
			}
		});

		return table;
	}

	private JTextField createFilterEditor() {
		final JTextField editor = new JTextField() {

			@Override
			protected void processKeyEvent(KeyEvent evt) {
				int vk = evt.getKeyCode();

				// redirect navigation events to subtitle table
				if (vk == KeyEvent.VK_UP || vk == KeyEvent.VK_DOWN || vk == KeyEvent.VK_ENTER) {
					subtitleTable.dispatchEvent(evt);
					return;
				}

				// enable filter again
				if (vk == KeyEvent.VK_BACK_SPACE && !filterEditor.getText().isEmpty() && getTableFilter() == null) {
					setTableFilter(getText());
					return;
				}

				// default key processing
				super.processKeyEvent(evt);
			}
		};

		// update sequence and element filter on change
		editor.getDocument().addDocumentListener(new LazyDocumentListener(0) {

			@Override
			public void update(DocumentEvent e) {
				setTableFilter(editor.getText());
			}
		});

		return editor;
	}

	private RowFilter<?, ?> getTableFilter() {
		TableRowSorter<?> sorter = (TableRowSorter<?>) subtitleTable.getRowSorter();
		return sorter.getRowFilter();
	}

	private void setTableFilter(String filter) {
		// filter by words
		List<SubtitleFilter> filterList = new ArrayList<SubtitleFilter>();

		if (filter != null) {
			for (String word : filter.split("\\s+")) {
				if (word.length() > 0) {
					filterList.add(new SubtitleFilter(word));
				}
			}
		}

		TableRowSorter sorter = (TableRowSorter) subtitleTable.getRowSorter();
		sorter.setRowFilter(filterList.isEmpty() ? null : RowFilter.andFilter(filterList));

		filterEditor.setForeground(filterList.isEmpty() ? disabledFilterForeground : defaultFilterForeground);
	}

	public void setData(List<SubtitleElement> data) {
		model.setData(data);
	}

	public JLabel getTitleLabel() {
		return titleLabel;
	}

	public JLabel getInfoLabel() {
		return infoLabel;
	}

	private final Action clearFilterAction = new AbstractAction("Clear Filter", ResourceManager.getIcon("edit.clear")) {

		@Override
		public void actionPerformed(ActionEvent e) {
			filterEditor.setText("");
		}
	};

	private static class SubtitleFilter extends RowFilter<Object, Integer> {

		private final Pattern filter;

		public SubtitleFilter(String filter) {
			this.filter = compile(quote(filter), CASE_INSENSITIVE | UNICODE_CHARACTER_CLASS | CANON_EQ);
		}

		@Override
		public boolean include(Entry<?, ? extends Integer> entry) {
			SubtitleTableModel model = (SubtitleTableModel) entry.getModel();
			SubtitleElement element = model.getRow(entry.getIdentifier());

			return filter.matcher(element.getText()).find();
		}

	}

	private static class SubtitleTableModel extends AbstractTableModel {

		private List<SubtitleElement> data = emptyList();

		public void setData(List<SubtitleElement> data) {
			this.data = new ArrayList<SubtitleElement>(data);

			// update view
			fireTableDataChanged();
		}

		public SubtitleElement getRow(int row) {
			return data.get(row);
		}

		@Override
		public String getColumnName(int column) {
			switch (column) {
			case 0:
				return "#";
			case 1:
				return "Start";
			case 2:
				return "End";
			case 3:
				return "Text";
			default:
				return null;
			}
		}

		@Override
		public int getColumnCount() {
			return 4;
		}

		@Override
		public int getRowCount() {
			return data.size();
		}

		@Override
		public Class<?> getColumnClass(int column) {
			switch (column) {
			case 0:
				return Integer.class;
			case 1:
				return Date.class;
			case 2:
				return Date.class;
			case 3:
				return String.class;
			default:
				return null;
			}
		}

		@Override
		public Object getValueAt(int row, int column) {
			switch (column) {
			case 0:
				return row + 1;
			case 1:
				return getRow(row).getStart();
			case 2:
				return getRow(row).getEnd();
			case 3:
				return getRow(row).getText();
			default:
				return null;
			}
		}
	}

}
