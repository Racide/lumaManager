package manager.view;

import manager.model.Globals;
import manager.model.Profiles;
import manager.model.SearchResults;
import manager.model.SteamApp;
import manager.view.components.GTable;
import manager.view.components.MousePressed;
import manager.view.components.WindowClosing;
import org.jetbrains.annotations.Nullable;
import org.tinylog.Logger;
import org.tinylog.Supplier;

import javax.swing.*;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableColumnModel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import static manager.view.components.ViewGlobals.*;

public class Gui{
    private final JFrame frame;
    private final GTable tbResults;
    private final JButton btSettings;
    private final JButton btGenerate;
    private final JButton btQuery;
    private final JButton btAddGames;
    private final JButton btAddProfile;
    private final JButton btDelGames;
    private final JButton btDelProfile;
    private final JComboBox<Profiles.Profile> cbProfiles;
    private final JList<SteamApp> lsGames;
    private final JTextField tfSearch;
    private final JPanel containerQuery;
    private final JLabel lbVersion;

    public static Controller create(Listener listener){
        Supplier<Gui> guiSupplier = Gui::new;
        if(SwingUtilities.isEventDispatchThread()){
            return guiSupplier.get().new Controller(listener);
        }else{
            RunnableFuture<Gui> rf = new FutureTask<>(guiSupplier::get);
            SwingUtilities.invokeLater(rf);
            try{
                return rf.get().new Controller(listener);
            }catch(InterruptedException | ExecutionException ex){
                Logger.error(ex, "failed to instantiate gui+controller on EDT");
                throw new RuntimeException(ex);
            }
        }
    }

    private Gui(){
        frame = new JFrame("GreenLuma Manager");
        frame.setIconImages(Arrays.asList(new ImageIcon(Gui.class.getResource("/images/logo16.png")).getImage(),
                new ImageIcon(Gui.class.getResource("/images/logo32.png")).getImage(),
                new ImageIcon(Gui.class.getResource("/images/logo48.png")).getImage(),
                new ImageIcon(Gui.class.getResource("/images/logo256.png")).getImage()));
        frame.setMinimumSize(new Dimension(600, 300));
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        final JPanel containerFooter = new JPanel();
        containerFooter.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));
        frame.getContentPane().add(containerFooter, BorderLayout.SOUTH);
        final GridBagLayout gbl_containerFooter = new GridBagLayout();
        gbl_containerFooter.columnWeights = new double[]{1.0, 0.0, 1.0};
        gbl_containerFooter.rowWeights = new double[]{0.0};
        containerFooter.setLayout(gbl_containerFooter);

        btSettings = new JButton();
        btSettings.setOpaque(false);
        btSettings.setIcon(new ImageIcon(new ImageIcon(Gui.class.getResource("/images/cog.png")).getImage()
                                                                                                .getScaledInstance(30,
                                                                                                        30,
                                                                                                        Image.SCALE_SMOOTH)));
        final GridBagConstraints gbc_btSettings = new GridBagConstraints();
        gbc_btSettings.anchor = GridBagConstraints.SOUTHWEST;
        gbc_btSettings.insets = new Insets(10, 0, 0, 0);
        gbc_btSettings.gridx = 0;
        gbc_btSettings.gridy = 0;
        containerFooter.add(btSettings, gbc_btSettings);

        btGenerate = new JButton("Generate");
        btGenerate.setBorder(BorderFactory.createCompoundBorder(btGenerate.getBorder(),
                BorderFactory.createEmptyBorder(0, 50, 0, 50)));
        btGenerate.setFont(bigFont);
        final GridBagConstraints gbc_btGenerate = new GridBagConstraints();
        gbc_btGenerate.fill = GridBagConstraints.VERTICAL;
        gbc_btGenerate.gridx = 1;
        gbc_btGenerate.gridy = 0;
        containerFooter.add(btGenerate, gbc_btGenerate);

        lbVersion = new JLabel("version");
        lbVersion.setFont(defaultFont.deriveFont(Font.PLAIN, 12));
        final GridBagConstraints gbc_lbVersion = new GridBagConstraints();
        gbc_lbVersion.anchor = GridBagConstraints.SOUTHEAST;
        gbc_lbVersion.gridx = 2;
        gbc_lbVersion.gridy = 0;
        containerFooter.add(lbVersion, gbc_lbVersion);

        final JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(1.0);
        frame.getContentPane().add(splitPane, BorderLayout.CENTER);

        final JPanel containerSearch = new JPanel();
        containerSearch.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        containerSearch.setPreferredSize(new Dimension(600, 500));
        splitPane.setLeftComponent(containerSearch);
        final GridBagLayout gbl_containerSearch = new GridBagLayout();
        gbl_containerSearch.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
        gbl_containerSearch.rowWeights = new double[]{0.0, 0.0, 1.0, 0.0, Double.MIN_VALUE};
        containerSearch.setLayout(gbl_containerSearch);

        final JLabel lbTitle = new JLabel("GreenLuma Manager");
        lbTitle.setFont(bigFont);
        final GridBagConstraints gbc_lbTitle = new GridBagConstraints();
        gbc_lbTitle.anchor = GridBagConstraints.NORTH;
        gbc_lbTitle.insets = new Insets(0, 0, 5, 0);
        gbc_lbTitle.gridwidth = 2;
        gbc_lbTitle.gridx = 0;
        gbc_lbTitle.gridy = 0;
        containerSearch.add(lbTitle, gbc_lbTitle);

        tfSearch = new JTextField();
        tfSearch.putClientProperty("JTextField.placeholderText", "Search Game");
        tfSearch.setFont(mediumFont);
        final GridBagConstraints gbc_tfSearch = new GridBagConstraints();
        gbc_tfSearch.fill = GridBagConstraints.BOTH;
        gbc_tfSearch.insets = new Insets(0, 0, 5, 0);
        gbc_tfSearch.gridx = 0;
        gbc_tfSearch.gridy = 1;
        containerSearch.add(tfSearch, gbc_tfSearch);

        containerQuery = new JPanel();
        final GridBagConstraints gbc_containerQuery = new GridBagConstraints();
        gbc_containerQuery.anchor = GridBagConstraints.EAST;
        gbc_containerQuery.insets = new Insets(0, 0, 5, 0);
        gbc_containerQuery.gridx = 1;
        gbc_containerQuery.gridy = 1;
        containerSearch.add(containerQuery, gbc_containerQuery);
        containerQuery.setLayout(new CardLayout());

        btQuery = new JButton("Search");
        btQuery.setFont(mediumFont);
        containerQuery.add(btQuery, "btQuery");

        final JProgressBar pbQuery = new JProgressBar();
        pbQuery.setIndeterminate(true);
        containerQuery.add(pbQuery, "pbQuery");

        final JScrollPane scResults = new JScrollPane();
        scResults.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scResults.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        final GridBagConstraints gbc_scResults = new GridBagConstraints();
        gbc_scResults.gridwidth = 2;
        gbc_scResults.fill = GridBagConstraints.BOTH;
        gbc_scResults.insets = new Insets(0, 0, 5, 0);
        gbc_scResults.gridx = 0;
        gbc_scResults.gridy = 2;
        containerSearch.add(scResults, gbc_scResults);

        tbResults = new GTable();
        tbResults.setShowVerticalLines(true);
        tbResults.getTableHeader().setFont(mediumFont);
        scResults.setViewportView(tbResults);

        btAddGames = new JButton("Add Games");
        btAddGames.setFont(mediumFont);
        final GridBagConstraints gbc_btAdd = new GridBagConstraints();
        gbc_btAdd.insets = new Insets(0, 0, 0, 5);
        gbc_btAdd.anchor = GridBagConstraints.NORTHWEST;
        gbc_btAdd.gridx = 0;
        gbc_btAdd.gridy = 3;
        containerSearch.add(btAddGames, gbc_btAdd);

        final JPanel containerGames = new JPanel();
        containerGames.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        splitPane.setRightComponent(containerGames);
        final GridBagLayout gbl_containerGames = new GridBagLayout();
        gbl_containerGames.columnWeights = new double[]{1.0, 1.0};
        gbl_containerGames.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 1.0, 0.0};
        containerGames.setLayout(gbl_containerGames);

        final JLabel lbProfile = new JLabel("Profile");
        lbProfile.setFont(mediumFont);
        final GridBagConstraints gbc_lbProfile = new GridBagConstraints();
        gbc_lbProfile.anchor = GridBagConstraints.WEST;
        gbc_lbProfile.insets = new Insets(0, 0, 5, 0);
        gbc_lbProfile.gridx = 0;
        gbc_lbProfile.gridy = 0;
        containerGames.add(lbProfile, gbc_lbProfile);

        cbProfiles = new JComboBox<>();
        cbProfiles.setFont(mediumFont);
        final GridBagConstraints gbc_cbProfiles = new GridBagConstraints();
        gbc_cbProfiles.gridwidth = 2;
        gbc_cbProfiles.insets = new Insets(0, 0, 5, 0);
        gbc_cbProfiles.fill = GridBagConstraints.HORIZONTAL;
        gbc_cbProfiles.gridx = 0;
        gbc_cbProfiles.gridy = 1;
        containerGames.add(cbProfiles, gbc_cbProfiles);

        btAddProfile = new JButton("New Profile");
        btAddProfile.setFont(mediumFont);
        final GridBagConstraints gbc_btNewProfile = new GridBagConstraints();
        gbc_btNewProfile.anchor = GridBagConstraints.WEST;
        gbc_btNewProfile.insets = new Insets(0, 0, 5, 0);
        gbc_btNewProfile.gridx = 0;
        gbc_btNewProfile.gridy = 2;
        containerGames.add(btAddProfile, gbc_btNewProfile);

        btDelProfile = new JButton("Delete Profile");
        btDelProfile.setFont(mediumFont);
        GridBagConstraints gbc_btDelProfile = new GridBagConstraints();
        gbc_btDelProfile.anchor = GridBagConstraints.EAST;
        gbc_btDelProfile.insets = new Insets(0, 0, 5, 0);
        gbc_btDelProfile.gridx = 1;
        gbc_btDelProfile.gridy = 2;
        containerGames.add(btDelProfile, gbc_btDelProfile);

        final JLabel lbGames = new JLabel("Games List");
        lbGames.setFont(mediumFont);
        GridBagConstraints gbc_lbGames = new GridBagConstraints();
        gbc_lbGames.anchor = GridBagConstraints.WEST;
        gbc_lbGames.insets = new Insets(0, 0, 5, 0);
        gbc_lbGames.gridx = 0;
        gbc_lbGames.gridy = 3;
        containerGames.add(lbGames, gbc_lbGames);

        final JScrollPane scGames = new JScrollPane();
        scGames.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        GridBagConstraints gbc_scGames = new GridBagConstraints();
        gbc_scGames.insets = new Insets(0, 0, 5, 0);
        gbc_scGames.fill = GridBagConstraints.BOTH;
        gbc_scGames.gridwidth = 2;
        gbc_scGames.gridx = 0;
        gbc_scGames.gridy = 4;
        containerGames.add(scGames, gbc_scGames);

        lsGames = new JList<>();
        scGames.setViewportView(lsGames);

        btDelGames = new JButton("Remove Games");
        btDelGames.setFont(mediumFont);
        final GridBagConstraints gbc_btDelGames = new GridBagConstraints();
        gbc_btDelGames.anchor = GridBagConstraints.EAST;
        gbc_btDelGames.gridx = 1;
        gbc_btDelGames.gridy = 5;
        containerGames.add(btDelGames, gbc_btDelGames);
    }

    public class Controller implements ActionListener, WindowClosing, ListDataListener, MousePressed{
        private final Listener listener;

        public Controller(Listener listener){
            this.listener = listener;
        }

        public void initialize(){
            setSearchResults(new SearchResults(SearchResults.Status.OK));
            SwingUtilities.invokeLater(() -> {
                lbVersion.setText(Globals.version);

                frame.addWindowListener(this);
                btQuery.addActionListener(this);
                tfSearch.addActionListener(this);
                cbProfiles.addActionListener(this);
                btAddGames.addActionListener(this);
                btDelGames.addActionListener(this);
                btAddProfile.addActionListener(this);
                btDelProfile.addActionListener(this);
                btSettings.addActionListener(this);
                btGenerate.addActionListener(this);
                tbResults.addMouseListener(this);

                frame.pack();
                frame.setLocationRelativeTo(null);
                loading(true);
                frame.setVisible(true);
            });
        }

        public void dataLoaded(){
            SwingUtilities.invokeLater(() -> {
                loading(false);
                tfSearch.requestFocusInWindow();
            });
        }

        public void setProfiles(Profiles.Profile lastProfile, Profiles profiles){
            SwingUtilities.invokeLater(() -> {
                cbProfiles.setModel(profiles);
                if(Arrays.stream(profiles.getListDataListeners()).noneMatch(listener -> listener == this)){
                    profiles.addListDataListener(this);
                }
                cbProfiles.setSelectedItem(lastProfile);
            });
        }

        public int updateAvailable(String newVersion, boolean warn){
            SwingUtilities.invokeLater(() -> lbVersion.setText(lbVersion.getText() + "(OLD)"));
            if(warn){
                String[] options = new String[]{"Open webpage", "Don't ask again for this version", "Cancel"};
                return JOptionPane.showOptionDialog(frame,
                        "A new version is available on GitHub (" + newVersion + ")",
                        "New version available",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.INFORMATION_MESSAGE,
                        null,
                        options,
                        options[0]);
            }else{
                return JOptionPane.CLOSED_OPTION;
            }
        }

        public Optional<File> folderChooser(@Nullable File outputDir){
            final JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Select the output directory");
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fc.setCurrentDirectory(outputDir);
            if(fc.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION){
                return Optional.empty();
            }
            return Optional.of(fc.getSelectedFile());
        }

        public Optional<File> fileChooser(@Nullable File outputDir, @Nullable String... extensions){
            final JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Select a file");
            if(Optional.ofNullable(extensions).isPresent()){
                fc.setFileFilter(new FileFilter(){
                    @Override
                    public boolean accept(File f){
                        if(f.isDirectory()){
                            return true;
                        }
                        return Arrays.stream(extensions)
                                     .anyMatch((extension) -> extension != null && f.getName().endsWith(extension));
                    }

                    @Override
                    public String getDescription(){
                        return "Json files";
                    }
                });
            }
            fc.setCurrentDirectory(outputDir);
            if(fc.showOpenDialog(frame) != JFileChooser.APPROVE_OPTION){
                return Optional.empty();
            }
            return Optional.of(fc.getSelectedFile());
        }

        public Optional<File> fileSave(File outputFile){
            final JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Save file");
            fc.setSelectedFile(outputFile);
            if(fc.showSaveDialog(frame) != JFileChooser.APPROVE_OPTION){
                return Optional.empty();
            }
            String extension = outputFile.getName();
            if(extension.lastIndexOf('.') != -1){
                extension = extension.substring(extension.lastIndexOf('.'));
            }else{
                extension = "";
            }
            File file = fc.getSelectedFile();
            if(!file.getName().endsWith(extension)){
                file = new File(file.getAbsolutePath() + extension);
            }
            return Optional.of(file);
        }

        public void setSearchResults(SearchResults searchResults){
            SwingUtilities.invokeLater(() -> {
                tbResults.setModel(searchResults);
                tbResults.tableColumnAdjuster.adjustColumns();
                final TableColumnModel tcm = tbResults.getColumnModel();
                tcm.getColumn(1)
                   .setWidth(tbResults.getParent().getWidth() - tcm.getColumn(0).getWidth() - tcm.getColumn(2)
                                                                                                 .getWidth());
                tbResults.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
                searchResultsStatus(searchResults.status);
                loading(false);
                tbResults.requestFocusInWindow();
            });
        }

        public void failingHttpStatusCodeError(int error){
            JOptionPane.showMessageDialog(frame,
                    "SteamDB responded with a status of " + error + ".",
                    "SteamDB http status error",
                    JOptionPane.ERROR_MESSAGE);
        }

        public void elementNotFoundError(){
            JOptionPane.showMessageDialog(frame,
                    "Critical page components couldn't be found on the result page.\nThis is likely caused by a website update.\nPlease create an issue with the log file attached.",
                    "Page scraping failed",
                    JOptionPane.ERROR_MESSAGE);
        }

        public void connectionError(){
            JOptionPane.showMessageDialog(frame,
                    "Couldn't connect to SteamDB",
                    "Connection failed",
                    JOptionPane.ERROR_MESSAGE);
        }

        public void readError(File file){
            JOptionPane.showMessageDialog(frame,
                    "Data couldn't be read from\n" + file.getAbsolutePath() + "\nCheck you have the required permissions.",
                    "Failed to read data",
                    JOptionPane.ERROR_MESSAGE);
        }

        public void writeError(File file){
            JOptionPane.showMessageDialog(frame,
                    "Data couldn't be written to\n" + file.getAbsolutePath() + "\nCheck you have the required permissions.",
                    "Failed to write data",
                    JOptionPane.ERROR_MESSAGE);
        }

        private Optional<String> showNewProfile(){
            String s = (String) JOptionPane.showInputDialog(frame,
                    "Profile name:",
                    "New profile",
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    null,
                    "Custom");
            if(s == null || s.trim().length() < 1){
                return Optional.empty();
            }
            return Optional.of(s.trim());
        }

        public Settings.Controller createSettings(Settings.Listener listener){
            return Settings.create(frame, listener);
        }

        // ↓ EDT ONLY ↓

        @Override
        public void intervalAdded(ListDataEvent e){
            ListModel<?> model = (ListModel<?>) e.getSource();
            if(model == cbProfiles.getModel()){
                cbProfiles.setSelectedIndex(e.getIndex0());
            }
        }

        @Override
        public void intervalRemoved(ListDataEvent e){
            ListModel<?> model = (ListModel<?>) e.getSource();
            if(model == cbProfiles.getModel()){
                cbProfiles.setSelectedIndex(e.getIndex0() >= 1 ? e.getIndex0() - 1 : 0);
            }
        }

        @Override
        public void contentsChanged(ListDataEvent e){
        }

        @Override
        public void actionPerformed(ActionEvent e){
            final JComponent s = (JComponent) e.getSource();
            if(s == tfSearch || s == btQuery){
                String query = tfSearch.getText();
                if(query.length() >= 1){
                    loading(true);
                    listener.search(tfSearch.getText());
                }
            }else if(s == cbProfiles){
                setProfile((Profiles.Profile) cbProfiles.getSelectedItem());
            }else if(s == btAddGames){
                listener.addGames((Profiles.Profile) cbProfiles.getSelectedItem(),
                        (SearchResults) tbResults.getModel(),
                        Arrays.stream(tbResults.getSelectedRows())
                              .map(tbResults.getRowSorter()::convertRowIndexToModel)
                              .toArray());
            }else if(s == btDelGames){
                listener.delGames((Profiles.Profile) cbProfiles.getSelectedItem(),
                        Arrays.stream(lsGames.getSelectedIndices()).map(i -> ~i).sorted().map(i -> ~i).toArray());
            }else if(s == btAddProfile){
                showNewProfile().ifPresent(listener::addProfile);
            }else if(s == btDelProfile){
                listener.delProfile(cbProfiles.getSelectedIndex());
            }else if(s == btSettings){
                listener.settings();
            }else if(s == btGenerate){
                loading(true);
                listener.generate((Profiles.Profile) cbProfiles.getSelectedItem()); // IO short enough for EDT
                loading(false);
            }
        }

        @Override
        public void windowClosing(WindowEvent e){
            loading(true);
            listener.close(); // IO short enough for EDT
            System.exit(0);
        }

        @Override
        public void mousePressed(MouseEvent e){
            final JComponent s = (JComponent) e.getSource();
            if(s == tbResults){
                if(SwingUtilities.isRightMouseButton(e)){
                    int row = tbResults.rowAtPoint(e.getPoint());
                    int col = tbResults.columnAtPoint(e.getPoint());
                    if(row >= 0 && col >= 0){
                        row = tbResults.convertRowIndexToModel(row);
                        col = tbResults.convertColumnIndexToModel(col);
                        addToClipboard(tbResults.getModel().getValueAt(row, col).toString());
                    }
                }
            }
        }

        private void loading(boolean loading){
            if(loading){
                ((CardLayout) containerQuery.getLayout()).show(containerQuery, "pbQuery");
                tfSearch.removeActionListener(this);
            }else{
                ((CardLayout) containerQuery.getLayout()).show(containerQuery, "btQuery");
                if(Arrays.stream(tfSearch.getActionListeners()).noneMatch(listener -> listener == this)){
                    tfSearch.addActionListener(this);
                }
            }
            btSettings.setEnabled(!loading);
            btGenerate.setEnabled(!loading);
            btQuery.setEnabled(!loading);
            btAddGames.setEnabled(!loading);
            btAddProfile.setEnabled(!loading);
            btDelGames.setEnabled(!loading);
            btDelProfile.setEnabled(!loading);
            frame.requestFocusInWindow();
        }

        private void setProfile(Profiles.Profile profile){
            lsGames.setModel(profile);
        }

        private void searchResultsStatus(SearchResults.Status status){
            tfSearch.putClientProperty("JComponent.outline", status.value);
            tfSearch.repaint(); // makes outline immediately visible
        }

        private boolean addToClipboard(String text){
            try{
                Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(text), null);
                return true;
            }catch(IllegalStateException ex){
                Logger.warn(ex, "failed to set clipboard contents");
                return false;
            }
        }
    }

    public interface Listener{
        void search(String query);

        void addGames(Profiles.Profile profile, SearchResults searchResults, int[] indices);

        void delGames(Profiles.Profile profile, int[] indices);

        void addProfile(String name);

        void delProfile(int index);

        void generate(Profiles.Profile profile);

        void close();

        void settings();
    }
}
