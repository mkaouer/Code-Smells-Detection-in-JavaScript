package net.filebot.ui.rename;

import static java.awt.event.KeyEvent.*;
import static javax.swing.JOptionPane.*;
import static javax.swing.KeyStroke.*;
import static javax.swing.SwingUtilities.*;
import static net.filebot.Logging.*;
import static net.filebot.Settings.*;
import static net.filebot.util.ExceptionUtilities.*;
import static net.filebot.util.FileUtilities.*;
import static net.filebot.util.ui.LoadingOverlayPane.*;
import static net.filebot.util.ui.SwingUI.*;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Insets;
import java.awt.Window;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;

import com.cedarsoftware.util.io.JsonReader;
import com.cedarsoftware.util.io.JsonWriter;
import com.google.common.eventbus.Subscribe;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.ListSelection;
import ca.odell.glazedlists.swing.DefaultEventSelectionModel;
import net.filebot.History;
import net.filebot.HistorySpooler;
import net.filebot.Language;
import net.filebot.ResourceManager;
import net.filebot.Settings;
import net.filebot.StandardRenameAction;
import net.filebot.UserFiles;
import net.filebot.WebServices;
import net.filebot.format.MediaBindingBean;
import net.filebot.mac.MacAppUtilities;
import net.filebot.similarity.Match;
import net.filebot.ui.rename.FormatDialog.Mode;
import net.filebot.ui.rename.RenameModel.FormattedFuture;
import net.filebot.ui.transfer.BackgroundFileTransferablePolicy;
import net.filebot.ui.transfer.TransferablePolicy;
import net.filebot.ui.transfer.TransferablePolicy.TransferAction;
import net.filebot.util.PreferencesMap.PreferencesEntry;
import net.filebot.util.ui.ActionPopup;
import net.filebot.util.ui.LoadingOverlayPane;
import net.filebot.vfs.FileInfo;
import net.filebot.web.AudioTrack;
import net.filebot.web.AudioTrackFormat;
import net.filebot.web.Episode;
import net.filebot.web.EpisodeFormat;
import net.filebot.web.EpisodeListProvider;
import net.filebot.web.Movie;
import net.filebot.web.MovieFormat;
import net.filebot.web.MovieIdentificationService;
import net.filebot.web.MusicIdentificationService;
import net.filebot.web.SortOrder;
import net.miginfocom.swing.MigLayout;

public class RenamePanel extends JComponent {

	public static final String MATCH_MODE_OPPORTUNISTIC = "Opportunistic";
	public static final String MATCH_MODE_STRICT = "Strict";

	protected final RenameModel renameModel = new RenameModel();

	protected final RenameList<FormattedFuture> namesList = new RenameList<FormattedFuture>(renameModel.names());

	protected final RenameList<File> filesList = new RenameList<File>(renameModel.files());

	protected final MatchAction matchAction = new MatchAction(renameModel);

	protected final RenameAction renameAction = new RenameAction(renameModel);

	private static final PreferencesEntry<String> persistentEpisodeFormat = Settings.forPackage(RenamePanel.class).entry("rename.format.episode");
	private static final PreferencesEntry<String> persistentMovieFormat = Settings.forPackage(RenamePanel.class).entry("rename.format.movie");
	private static final PreferencesEntry<String> persistentMusicFormat = Settings.forPackage(RenamePanel.class).entry("rename.format.music");
	private static final PreferencesEntry<String> persistentFileFormat = Settings.forPackage(RenamePanel.class).entry("rename.format.file");

	private static final PreferencesEntry<String> persistentLastFormatState = Settings.forPackage(RenamePanel.class).entry("rename.last.format.state").defaultValue(Mode.Episode.name());
	private static final PreferencesEntry<String> persistentPreferredMatchMode = Settings.forPackage(RenamePanel.class).entry("rename.match.mode").defaultValue(MATCH_MODE_OPPORTUNISTIC);
	private static final PreferencesEntry<String> persistentPreferredLanguage = Settings.forPackage(RenamePanel.class).entry("rename.language").defaultValue("en");
	private static final PreferencesEntry<String> persistentPreferredEpisodeOrder = Settings.forPackage(RenamePanel.class).entry("rename.episode.order").defaultValue("Airdate");

	public RenamePanel() {
		namesList.setTitle("New Names");
		namesList.setTransferablePolicy(new NamesListTransferablePolicy(renameModel.values()));

		filesList.setTitle("Original Files");
		filesList.setTransferablePolicy(new FilesListTransferablePolicy(renameModel.files()));

		// restore icon indicating current match mode
		matchAction.setMatchMode(isMatchModeStrict());

		try {
			// restore custom episode formatter
			renameModel.useFormatter(Episode.class, new ExpressionFormatter(persistentEpisodeFormat.getValue(), EpisodeFormat.SeasonEpisode, Episode.class));
		} catch (Exception e) {
			// use default formatter
		}

		try {
			// restore custom movie formatter
			renameModel.useFormatter(Movie.class, new ExpressionFormatter(persistentMovieFormat.getValue(), MovieFormat.NameYear, Movie.class));
		} catch (Exception e) {
			// use default movie formatter
			renameModel.useFormatter(Movie.class, new MovieFormatter());
		}

		try {
			// restore custom music formatter
			renameModel.useFormatter(AudioTrack.class, new ExpressionFormatter(persistentMusicFormat.getValue(), new AudioTrackFormat(), AudioTrack.class));
		} catch (Exception e) {
			// use default formatter
		}

		try {
			// restore custom music formatter
			renameModel.useFormatter(File.class, new ExpressionFormatter(persistentFileFormat.getValue(), new FileNameFormat(), File.class));
		} catch (Exception e) {
			// make sure to put File formatter at position 3
			renameModel.useFormatter(File.class, new FileNameFormatter(renameModel.preserveExtension()));
		} finally {
			// use default filename formatter
			renameModel.useFormatter(FileInfo.class, new FileNameFormatter(renameModel.preserveExtension()));
		}

		RenameListCellRenderer cellrenderer = new RenameListCellRenderer(renameModel, ApplicationFolder.UserHome.getCanonicalFile());

		namesList.getListComponent().setCellRenderer(cellrenderer);
		filesList.getListComponent().setCellRenderer(cellrenderer);

		DefaultEventSelectionModel<Match<Object, File>> selectionModel = new DefaultEventSelectionModel<Match<Object, File>>(renameModel.matches());
		selectionModel.setSelectionMode(ListSelection.SINGLE_SELECTION);

		// use the same selection model for both lists to synchronize selection
		namesList.getListComponent().setSelectionModel(selectionModel);
		filesList.getListComponent().setSelectionModel(selectionModel);

		// synchronize viewports
		new ScrollPaneSynchronizer(namesList, filesList);

		// delete items from both lists
		Action removeAction = newAction("Exclude Selected Items", ResourceManager.getIcon("dialog.cancel"), evt -> {
			RenameList list = null;
			boolean deleteCell;

			if (evt.getSource() instanceof JButton) {
				list = filesList;
				deleteCell = isShiftOrAltDown(evt);
			} else {
				list = ((RenameList) evt.getSource());
				deleteCell = isShiftOrAltDown(evt);
			}

			int index = list.getListComponent().getSelectedIndex();
			if (index >= 0) {
				if (deleteCell) {
					EventList eventList = list.getModel();
					if (index < eventList.size()) {
						list.getModel().remove(index);
					}
				} else {
					renameModel.matches().remove(index);
				}
				int maxIndex = list.getModel().size() - 1;
				if (index > maxIndex) {
					index = maxIndex;
				}
				if (index >= 0) {
					list.getListComponent().setSelectedIndex(index);
				}
			}
		});
		namesList.setRemoveAction(removeAction);
		filesList.setRemoveAction(removeAction);

		// create Match button
		JButton matchButton = new JButton(matchAction);
		matchButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		matchButton.setHorizontalTextPosition(SwingConstants.CENTER);

		// create Rename button
		JButton renameButton = new JButton(renameAction);
		renameButton.setVerticalTextPosition(SwingConstants.BOTTOM);
		renameButton.setHorizontalTextPosition(SwingConstants.CENTER);

		// create fetch popup
		ActionPopup fetchPopup = createFetchPopup();

		Action fetchPopupAction = new ShowPopupAction("Fetch Data", ResourceManager.getIcon("action.fetch"));
		JButton fetchButton = new JButton(fetchPopupAction);
		filesList.getListComponent().setComponentPopupMenu(fetchPopup);
		namesList.getListComponent().setComponentPopupMenu(fetchPopup);
		fetchButton.setComponentPopupMenu(fetchPopup);
		matchButton.setComponentPopupMenu(fetchPopup);
		namesList.getButtonPanel().add(fetchButton, "gap 0");

		namesList.getListComponent().setComponentPopupMenu(fetchPopup);
		fetchButton.setComponentPopupMenu(fetchPopup);

		// settings popup and button
		ActionPopup settingsPopup = createSettingsPopup();
		final Action settingsPopupAction = new ShowPopupAction("Settings", ResourceManager.getIcon("action.settings"));
		JButton settingsButton = createImageButton(settingsPopupAction);
		settingsButton.setComponentPopupMenu(settingsPopup);
		renameButton.setComponentPopupMenu(settingsPopup);
		namesList.getButtonPanel().add(settingsButton, "gap indent");

		// open rename log button
		filesList.getButtonPanel().add(createImageButton(removeAction), "gap 0", 2);
		filesList.getButtonPanel().add(createImageButton(clearFilesAction), "gap 0");
		filesList.getButtonPanel().add(createImageButton(openHistoryAction), "gap indent");

		// create macros popup
		final Action macrosAction = new ShowPresetsPopupAction("Presets", ResourceManager.getIcon("action.script"));
		JButton macrosButton = createImageButton(macrosAction);
		filesList.getButtonPanel().add(macrosButton, "gap 0");

		// show popup on actionPerformed only when names list is empty
		matchButton.addActionListener(evt -> {
			if (renameModel.names().isEmpty()) {
				fetchPopupAction.actionPerformed(evt);
			}
		});

		// reveal file location on double click
		filesList.getListComponent().addMouseListener(mouseClicked(evt -> {
			if (evt.getClickCount() == 2) {
				getWindow(evt.getSource()).setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
				try {
					JList list = (JList) evt.getSource();
					if (list.getSelectedIndex() >= 0) {
						UserFiles.revealFiles(list.getSelectedValuesList());
					}
				} catch (Exception e) {
					debug.log(Level.WARNING, e.getMessage(), e);
				} finally {
					getWindow(evt.getSource()).setCursor(Cursor.getDefaultCursor());
				}
			}
		}));

		// reveal file location on double click
		namesList.getListComponent().addMouseListener(mouseClicked(evt -> {
			if (evt.getClickCount() == 2) {
				JList list = (JList) evt.getSource();
				if (list.getSelectedIndex() >= 0) {
					try {
						withWaitCursor(list, () -> {
							Match<Object, File> match = renameModel.getMatch(list.getSelectedIndex());
							Map<File, Object> context = renameModel.getMatchContext(match);

							MediaBindingBean sample = new MediaBindingBean(match.getValue(), match.getCandidate(), context);
							showFormatEditor(sample);
						});
					} catch (Exception e) {
						debug.log(Level.WARNING, e.getMessage(), e);
					}
				}
			}
		}));

		setLayout(new MigLayout("fill, insets dialog, gapx 10px", "[fill][align center, pref!][fill]", "align 33%"));
		add(new LoadingOverlayPane(filesList, filesList, "37px", "30px"), "grow, sizegroupx list");

		BackgroundFileTransferablePolicy<?> transferablePolicy = (BackgroundFileTransferablePolicy<?>) filesList.getTransferablePolicy();
		transferablePolicy.addPropertyChangeListener(evt -> {
			if (BackgroundFileTransferablePolicy.LOADING_PROPERTY.equals(evt.getPropertyName())) {
				filesList.firePropertyChange(LoadingOverlayPane.LOADING_PROPERTY, (boolean) evt.getOldValue(), (boolean) evt.getNewValue());
			}
		});

		// make buttons larger
		matchButton.setMargin(new Insets(3, 14, 2, 14));
		renameButton.setMargin(new Insets(6, 11, 2, 11));

		add(matchButton, "split 2, flowy, sizegroupx button");
		add(renameButton, "gapy 30px, sizegroupx button");

		add(new LoadingOverlayPane(namesList, namesList, "37px", "30px"), "grow, sizegroupx list");

		// manual force name via F2
		installAction(namesList.getListComponent(), getKeyStroke(VK_F2, 0), newAction("Force Name", evt -> {
			try {
				if (namesList.getModel().isEmpty()) {
					withWaitCursor(evt.getSource(), () -> {
						// try to read xattr from all files
						Map<File, Object> xattr = WebServices.XattrMetaData.getMetaData(renameModel.files());

						// upper list is based on xattr metadata
						List<File> files = new ArrayList<File>(xattr.keySet());
						List<Object> objects = new ArrayList<Object>(xattr.values());

						// lower list is just the fallback file object
						renameModel.files().stream().filter(f -> !xattr.containsKey(f)).forEach(f -> {
							files.add(f);
							objects.add(f);
						});

						renameModel.clear();
						renameModel.addAll(objects, files);
					});
				} else {
					int index = namesList.getListComponent().getSelectedIndex();
					if (index >= 0) {
						File file = (File) filesList.getListComponent().getModel().getElementAt(index);
						Object object = namesList.getListComponent().getModel().getElementAt(index);

						String string = showInputDialog("Enter Name:", object.toString(), "Enter Name", RenamePanel.this);
						if (string != null && string.length() > 0) {
							renameModel.matches().set(index, new Match<Object, File>(string + '.' + getExtension(file), file));
						}
					}
				}
			} catch (Exception e) {
				debug.log(Level.WARNING, e.getMessage(), e);
			}
		}));
	}

	private boolean isMatchModeStrict() {
		return MATCH_MODE_STRICT.equalsIgnoreCase(persistentPreferredMatchMode.getValue());
	}

	protected ActionPopup createPresetsPopup() {
		Map<String, String> persistentPresets = Settings.forPackage(RenamePanel.class).node("presets").asMap();
		ActionPopup actionPopup = new ActionPopup("Presets", ResourceManager.getIcon("action.script"));

		if (persistentPresets.size() > 0) {
			for (String it : persistentPresets.values()) {
				try {
					Preset p = (Preset) JsonReader.jsonToJava(it);
					actionPopup.add(new ApplyPresetAction(p));
				} catch (Exception e) {
					debug.log(Level.SEVERE, e.getMessage(), e);
				}
			}
			actionPopup.addSeparator();
		}

		actionPopup.add(newAction("Edit Presets", ResourceManager.getIcon("script.add"), evt -> {
			try {
				String newPresetOption = "New Preset …";
				List<String> presetNames = new ArrayList<String>(persistentPresets.keySet());
				presetNames.add(newPresetOption);

				String selection = (String) showInputDialog(getWindow(evt.getSource()), "Edit or create a preset:", "Edit Preset", PLAIN_MESSAGE, null, presetNames.toArray(), newPresetOption);
				if (selection == null)
					return;

				Preset preset = null;
				if (selection == newPresetOption) {
					selection = (String) showInputDialog(getWindow(evt.getSource()), "Preset Name:", newPresetOption, PLAIN_MESSAGE, null, null, "My Preset");
					if (selection == null || selection.trim().isEmpty())
						return;

					preset = new Preset(selection.trim(), null, null, null, null, null, null, null, null);
				} else {
					preset = (Preset) JsonReader.jsonToJava(persistentPresets.get(selection.toString()));
				}

				PresetEditor presetEditor = new PresetEditor(getWindow(evt.getSource()));
				presetEditor.setLocation(getOffsetLocation(presetEditor.getOwner()));
				presetEditor.setPreset(preset);
				presetEditor.setVisible(true);

				switch (presetEditor.getResult()) {
				case SET:
					preset = presetEditor.getPreset();
					persistentPresets.put(selection, JsonWriter.objectToJson(preset));
					break;
				case DELETE:
					persistentPresets.remove(selection);
					break;
				case CANCEL:
					break;
				}
			} catch (Exception e) {
				debug.log(Level.WARNING, e.toString());
			}
		}));

		return actionPopup;
	}

	protected ActionPopup createFetchPopup() {
		ActionPopup actionPopup = new ActionPopup("Fetch & Match Data", ResourceManager.getIcon("action.fetch"));

		actionPopup.addDescription(new JLabel("Episode Mode:"));

		// create actions for match popup episode list completion
		for (EpisodeListProvider db : WebServices.getEpisodeListProviders()) {
			actionPopup.add(new AutoCompleteAction(db.getName(), db.getIcon(), new EpisodeListMatcher(db, db == WebServices.AniDB)));
		}

		actionPopup.addSeparator();
		actionPopup.addDescription(new JLabel("Movie Mode:"));

		// create action for movie name completion
		for (MovieIdentificationService it : WebServices.getMovieIdentificationServices()) {
			actionPopup.add(new AutoCompleteAction(it.getName(), it.getIcon(), new MovieMatcher(it)));
		}

		actionPopup.addSeparator();
		actionPopup.addDescription(new JLabel("Music Mode:"));
		for (MusicIdentificationService it : WebServices.getMusicIdentificationServices()) {
			actionPopup.add(new AutoCompleteAction(it.getName(), it.getIcon(), new MusicMatcher(it)));
		}

		actionPopup.addSeparator();
		actionPopup.addDescription(new JLabel("Smart Mode:"));
		actionPopup.add(new AutoCompleteAction("Autodetect", ResourceManager.getIcon("action.auto"), new AutoDetectMatcher()));

		actionPopup.addSeparator();
		actionPopup.addDescription(new JLabel("Options:"));

		actionPopup.add(newAction("Edit Format", ResourceManager.getIcon("action.format"), evt -> {
			showFormatEditor(null);
		}));

		actionPopup.add(newAction("Preferences", ResourceManager.getIcon("action.preferences"), evt -> {
			String[] modes = new String[] { MATCH_MODE_OPPORTUNISTIC, MATCH_MODE_STRICT };
			JComboBox modeCombo = new JComboBox(modes);

			List<Language> languages = new ArrayList<Language>();
			languages.addAll(Language.preferredLanguages()); // add preferred languages first
			languages.addAll(Language.availableLanguages()); // then others

			JComboBox orderCombo = new JComboBox(SortOrder.values());
			JList languageList = new JList(languages.toArray());
			languageList.setCellRenderer(new DefaultListCellRenderer() {

				@Override
				public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
					super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					if (value != null) {
						setText(((Language) value).getName());
						setIcon(ResourceManager.getFlagIcon(((Language) value).getCode()));
					}
					return this;
				}
			});

			// restore current preference values
			try {
				modeCombo.setSelectedItem(persistentPreferredMatchMode.getValue());
				for (Language language : languages) {
					if (language.getCode().equals(persistentPreferredLanguage.getValue())) {
						languageList.setSelectedValue(language, true);
						break;
					}
				}
				orderCombo.setSelectedItem(SortOrder.forName(persistentPreferredEpisodeOrder.getValue()));
			} catch (Exception e) {
				debug.log(Level.WARNING, e.getMessage(), e);
			}

			JScrollPane spModeCombo = new JScrollPane(modeCombo, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			spModeCombo.setBorder(new CompoundBorder(new TitledBorder("Match Mode"), spModeCombo.getBorder()));
			JScrollPane spLanguageList = new JScrollPane(languageList);
			spLanguageList.setBorder(new CompoundBorder(new TitledBorder("Language"), spLanguageList.getBorder()));
			JScrollPane spOrderCombo = new JScrollPane(orderCombo, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			spOrderCombo.setBorder(new CompoundBorder(new TitledBorder("Episode Order"), spOrderCombo.getBorder()));

			// fix background issues on OSX
			spModeCombo.setOpaque(false);
			spLanguageList.setOpaque(false);
			spOrderCombo.setOpaque(false);

			JPanel message = new JPanel(new MigLayout("fill, flowy, insets 0"));
			message.add(spModeCombo, "grow, hmin 24px");
			message.add(spLanguageList, "grow, hmin 50px");
			message.add(spOrderCombo, "grow, hmin 24px");
			JOptionPane pane = new JOptionPane(message, PLAIN_MESSAGE, OK_CANCEL_OPTION);
			pane.createDialog(getWindowAncestor(RenamePanel.this), "Preferences").setVisible(true);

			if (pane.getValue() != null && pane.getValue().equals(OK_OPTION)) {
				persistentPreferredMatchMode.setValue((String) modeCombo.getSelectedItem());
				persistentPreferredLanguage.setValue(((Language) languageList.getSelectedValue()).getCode());
				persistentPreferredEpisodeOrder.setValue(((SortOrder) orderCombo.getSelectedItem()).name());

				// update UI
				matchAction.setMatchMode(isMatchModeStrict());
			}
		}));

		return actionPopup;
	}

	protected ActionPopup createSettingsPopup() {
		ActionPopup actionPopup = new ActionPopup("Rename Options", ResourceManager.getIcon("action.settings"));

		actionPopup.addDescription(new JLabel("Extension:"));
		actionPopup.add(new SetRenameMode(false, "Preserve", ResourceManager.getIcon("action.extension.preserve")));
		actionPopup.add(new SetRenameMode(true, "Override", ResourceManager.getIcon("action.extension.override")));

		actionPopup.addSeparator();

		actionPopup.addDescription(new JLabel("Action:"));
		for (StandardRenameAction action : EnumSet.of(StandardRenameAction.MOVE, StandardRenameAction.COPY, StandardRenameAction.KEEPLINK, StandardRenameAction.SYMLINK, StandardRenameAction.HARDLINK)) {
			actionPopup.add(new SetRenameAction(action));
		}

		return actionPopup;
	}

	protected void showFormatEditor(MediaBindingBean lockOnBinding) {
		// default to Episode mode
		Mode initMode = Mode.Episode;

		if (lockOnBinding != null) {
			if (lockOnBinding.getInfoObject() instanceof Episode) {
				initMode = Mode.Episode;
			} else if (lockOnBinding.getInfoObject() instanceof Movie) {
				initMode = Mode.Movie;
			} else if (lockOnBinding.getInfoObject() instanceof AudioTrack) {
				initMode = Mode.Music;
			} else if (lockOnBinding.getInfoObject() instanceof File) {
				initMode = Mode.File;
			} else {
				return; // ignore objects that cannot be formatted
			}
		} else {
			try {
				initMode = Mode.valueOf(persistentLastFormatState.getValue()); // restore previous mode
			} catch (Exception e) {
				debug.log(Level.WARNING, e.getMessage(), e);
			}
		}

		FormatDialog dialog = new FormatDialog(getWindowAncestor(RenamePanel.this), initMode, lockOnBinding);
		dialog.setLocation(getOffsetLocation(dialog.getOwner()));
		dialog.setVisible(true);

		if (dialog.submit()) {
			switch (dialog.getMode()) {
			case Episode:
				renameModel.useFormatter(Episode.class, new ExpressionFormatter(dialog.getFormat().getExpression(), EpisodeFormat.SeasonEpisode, Episode.class));
				persistentEpisodeFormat.setValue(dialog.getFormat().getExpression());
				break;
			case Movie:
				renameModel.useFormatter(Movie.class, new ExpressionFormatter(dialog.getFormat().getExpression(), MovieFormat.NameYear, Movie.class));
				persistentMovieFormat.setValue(dialog.getFormat().getExpression());
				break;
			case Music:
				renameModel.useFormatter(AudioTrack.class, new ExpressionFormatter(dialog.getFormat().getExpression(), new AudioTrackFormat(), AudioTrack.class));
				persistentMusicFormat.setValue(dialog.getFormat().getExpression());
				break;
			case File:
				renameModel.useFormatter(File.class, new ExpressionFormatter(dialog.getFormat().getExpression(), new FileNameFormat(), File.class));
				persistentFileFormat.setValue(dialog.getFormat().getExpression());
				break;
			}

			if (lockOnBinding == null) {
				persistentLastFormatState.setValue(dialog.getMode().name());
			}
		}
	}

	protected final Action clearFilesAction = newAction("Clear All", ResourceManager.getIcon("action.clear"), evt -> {
		if (isShiftOrAltDown(evt)) {
			renameModel.files().clear();
		} else {
			renameModel.clear();
		}
	});

	protected final Action openHistoryAction = newAction("Open History", ResourceManager.getIcon("action.report"), evt -> {
		try {
			History model = HistorySpooler.getInstance().getCompleteHistory();

			HistoryDialog dialog = new HistoryDialog(getWindow(RenamePanel.this));
			dialog.setLocationRelativeTo(RenamePanel.this);
			dialog.setModel(model);

			// show and block
			dialog.setVisible(true);
		} catch (Exception e) {
			log.log(Level.WARNING, String.format("%s: %s", getRootCause(e).getClass().getSimpleName(), getRootCauseMessage(e)), e);
		}
	});

	@Subscribe
	public void handle(Transferable transferable) throws Exception {
		for (TransferablePolicy handler : new TransferablePolicy[] { filesList.getTransferablePolicy(), namesList.getTransferablePolicy() }) {
			if (handler != null && handler.accept(transferable)) {
				handler.handleTransferable(transferable, TransferAction.PUT);
				return;
			}
		}
	}

	protected static class ShowPopupAction extends AbstractAction {

		public ShowPopupAction(String name, Icon icon) {
			super(name, icon);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// display popup below component
			JComponent source = (JComponent) e.getSource();
			source.getComponentPopupMenu().show(source, -3, source.getHeight() + 4);
		}
	};

	protected class ShowPresetsPopupAction extends AbstractAction {

		public ShowPresetsPopupAction(String name, Icon icon) {
			super(name, icon);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// display popup below component
			JComponent source = (JComponent) e.getSource();
			createPresetsPopup().show(source, -3, source.getHeight() + 4);
		}
	};

	protected class ApplyPresetAction extends AutoCompleteAction {

		private Preset preset;

		public ApplyPresetAction(Preset preset) {
			super(preset.getName(), ResourceManager.getIcon("script.go"), preset.getAutoCompleteMatcher());
			this.preset = preset;
		}

		@Override
		public List<File> getFiles(ActionEvent evt) {
			List<File> input = preset.selectInputFiles(evt);

			if (input != null) {
				renameModel.clear();
				renameModel.files().addAll(input);
			} else {
				input = new ArrayList<File>(super.getFiles(evt));
			}

			if (input.isEmpty()) {
				throw new IllegalStateException("No files selected.");
			}
			return input;
		}

		@Override
		public boolean isStrict(ActionEvent evt) {
			return preset.getMatchMode() != null ? MATCH_MODE_STRICT.equals(preset.getMatchMode()) : super.isStrict(evt);
		}

		@Override
		public SortOrder getSortOrder(ActionEvent evt) {
			return preset.getSortOrder() != null ? preset.getSortOrder() : super.getSortOrder(evt);
		}

		@Override
		public Locale getLocale(ActionEvent evt) {
			return preset.getLanguage() != null ? preset.getLanguage().getLocale() : super.getLocale(evt);
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			Window window = getWindow(RenamePanel.this);
			try {
				window.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

				if (preset.getFormat() != null) {
					switch (FormatDialog.Mode.getMode(preset.getDatasource())) {
					case Episode:
						renameModel.useFormatter(Episode.class, new ExpressionFormatter(preset.getFormat().getExpression(), EpisodeFormat.SeasonEpisode, Episode.class));
						break;
					case Movie:
						renameModel.useFormatter(Movie.class, new ExpressionFormatter(preset.getFormat().getExpression(), MovieFormat.NameYear, Movie.class));
						break;
					case Music:
						renameModel.useFormatter(AudioTrack.class, new ExpressionFormatter(preset.getFormat().getExpression(), new AudioTrackFormat(), AudioTrack.class));
						break;
					case File:
						renameModel.useFormatter(File.class, new ExpressionFormatter(preset.getFormat().getExpression(), new FileNameFormat(), File.class));
						break;
					}
				}

				if (preset.getRenameAction() != null) {
					new SetRenameAction(preset.getRenameAction()).actionPerformed(evt);
				}

				super.actionPerformed(evt);
			} catch (Exception e) {
				log.info(e.getMessage());
			} finally {
				window.setCursor(Cursor.getDefaultCursor());
			}
		}
	}

	protected class SetRenameMode extends AbstractAction {

		private final boolean activate;

		private SetRenameMode(boolean activate, String name, Icon icon) {
			super(name, icon);
			this.activate = activate;
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			renameModel.setPreserveExtension(!activate);

			// use different file name formatter
			renameModel.useFormatter(FileInfo.class, new FileNameFormatter(renameModel.preserveExtension()));

			// display changed state
			filesList.repaint();
		}
	}

	protected class SetRenameAction extends AbstractAction {

		private final StandardRenameAction action;

		public SetRenameAction(StandardRenameAction action) {
			super(action.getDisplayName(), ResourceManager.getIcon("rename.action." + action.name().toLowerCase()));
			this.action = action;
		}

		@Override
		public void actionPerformed(ActionEvent evt) {
			if (action == StandardRenameAction.MOVE) {
				renameAction.resetValues();
			} else {
				renameAction.putValue(RenameAction.RENAME_ACTION, action);
				renameAction.putValue(NAME, this.getValue(NAME));
				renameAction.putValue(SMALL_ICON, ResourceManager.getIcon("action." + action.name().toLowerCase()));
			}
		}
	}

	protected class AutoCompleteAction extends AbstractAction {

		protected final AutoCompleteMatcher matcher;

		public AutoCompleteAction(String name, Icon icon, AutoCompleteMatcher matcher) {
			super(name, icon);
			this.matcher = matcher;

			// disable action while episode list matcher is working
			namesList.addPropertyChangeListener(LOADING_PROPERTY, evt -> {
				// disable action while loading is in progress
				setEnabled(!(Boolean) evt.getNewValue());
			});
		}

		public List<File> getFiles(ActionEvent evt) {
			return renameModel.files();
		}

		public boolean isStrict(ActionEvent evt) {
			return isMatchModeStrict();
		}

		public SortOrder getSortOrder(ActionEvent evt) {
			return SortOrder.forName(persistentPreferredEpisodeOrder.getValue());
		}

		public Locale getLocale(ActionEvent evt) {
			return new Locale(persistentPreferredLanguage.getValue());
		}

		private boolean isAutoDetectionEnabled(ActionEvent evt) {
			return !isShiftOrAltDown(evt); // skip name auto-detection if SHIFT is pressed
		}

		@Override
		public void actionPerformed(final ActionEvent evt) {
			// clear names list
			renameModel.values().clear();

			final List<File> remainingFiles = new LinkedList<File>(getFiles(evt));
			final boolean strict = isStrict(evt);
			final SortOrder order = getSortOrder(evt);
			final Locale locale = getLocale(evt);
			final boolean autodetection = isAutoDetectionEnabled(evt);

			if (isMacSandbox()) {
				if (!MacAppUtilities.askUnlockFolders(getWindow(RenamePanel.this), remainingFiles)) {
					return;
				}
			}

			SwingWorker<List<Match<File, ?>>, Void> worker = new SwingWorker<List<Match<File, ?>>, Void>() {

				@Override
				protected List<Match<File, ?>> doInBackground() throws Exception {
					List<Match<File, ?>> matches = matcher.match(remainingFiles, strict, order, locale, autodetection, getWindow(RenamePanel.this));

					// remove matched files
					for (Match<File, ?> match : matches) {
						remainingFiles.remove(match.getValue());
					}

					return matches;
				}

				@Override
				protected void done() {
					try {
						List<Match<Object, File>> matches = new ArrayList<Match<Object, File>>();

						for (Match<File, ?> match : get()) {
							matches.add(new Match<Object, File>(match.getCandidate(), match.getValue()));
						}

						renameModel.clear();
						renameModel.addAll(matches);

						// add remaining file entries
						renameModel.files().addAll(remainingFiles);
					} catch (Exception e) {
						if (findCause(e, CancellationException.class) == null) {
							log.log(Level.WARNING, String.format("%s: %s", getRootCause(e).getClass().getSimpleName(), getRootCauseMessage(e)), e);
						}
					} finally {
						// auto-match finished
						namesList.firePropertyChange(LOADING_PROPERTY, true, false);
					}
				}
			};

			// auto-match in progress
			namesList.firePropertyChange(LOADING_PROPERTY, false, true);
			worker.execute();
		}

	}

}
