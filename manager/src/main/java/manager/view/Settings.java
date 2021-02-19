package manager.view;

import org.tinylog.Logger;
import org.tinylog.Supplier;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import static manager.view.components.ViewGlobals.mediumFont;

public class Settings{
    private final JDialog dialog;
    private final JTextField tfOutputPath;
    private final JButton btBrowse;
    private final JButton btShortcut;
    private final JButton btImport;

    public static Settings.Controller create(JFrame parent, Settings.Listener listener){
        Supplier<Settings> settingsSupplier = () -> new Settings(parent);
        if(SwingUtilities.isEventDispatchThread()){
            return settingsSupplier.get().new Controller(listener);
        }else{
            RunnableFuture<Settings> rf = new FutureTask<>(settingsSupplier::get);
            SwingUtilities.invokeLater(rf);
            try{
                return rf.get().new Controller(listener);
            }catch(InterruptedException | ExecutionException ex){
                Logger.error(ex, "failed to instantiate settings+controller on EDT");
                throw new RuntimeException(ex);
            }
        }
    }

    private Settings(JFrame parent){
        dialog = new JDialog(parent, "Settings", JDialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setType(JDialog.Type.POPUP);

        final JPanel contentPane = (JPanel) dialog.getContentPane();
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        final GridBagLayout gbl_containerSettings = new GridBagLayout();
        gbl_containerSettings.columnWeights = new double[]{1.0, 0.0};
        gbl_containerSettings.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0};
        contentPane.setLayout(gbl_containerSettings);

        final JLabel lbOutputPath = new JLabel("Output path:");
        lbOutputPath.setFont(mediumFont);
        final GridBagConstraints gbc_lbOutputPath = new GridBagConstraints();
        gbc_lbOutputPath.fill = GridBagConstraints.VERTICAL;
        gbc_lbOutputPath.anchor = GridBagConstraints.WEST;
        gbc_lbOutputPath.insets = new Insets(0, 0, 10, 5);
        gbc_lbOutputPath.gridx = 0;
        gbc_lbOutputPath.gridy = 0;
        contentPane.add(lbOutputPath, gbc_lbOutputPath);

        tfOutputPath = new JTextField();
        tfOutputPath.setFont(mediumFont);
        tfOutputPath.setEditable(false);
        final GridBagConstraints gbc_tfOutputPath = new GridBagConstraints();
        gbc_tfOutputPath.insets = new Insets(0, 0, 20, 5);
        gbc_tfOutputPath.fill = GridBagConstraints.BOTH;
        gbc_tfOutputPath.gridx = 0;
        gbc_tfOutputPath.gridy = 1;
        contentPane.add(tfOutputPath, gbc_tfOutputPath);

        btBrowse = new JButton("...");
        btBrowse.setFont(mediumFont);
        GridBagConstraints gbc_btBrowse = new GridBagConstraints();
        gbc_btBrowse.insets = new Insets(0, 0, 20, 0);
        gbc_btBrowse.fill = GridBagConstraints.BOTH;
        gbc_btBrowse.gridx = 1;
        gbc_btBrowse.gridy = 1;
        contentPane.add(btBrowse, gbc_btBrowse);

        final JLabel lbUtils = new JLabel("Utils:");
        lbUtils.setFont(mediumFont);
        final GridBagConstraints gbc_lbUtils = new GridBagConstraints();
        gbc_lbUtils.fill = GridBagConstraints.VERTICAL;
        gbc_lbUtils.anchor = GridBagConstraints.WEST;
        gbc_lbUtils.insets = new Insets(0, 0, 10, 5);
        gbc_lbUtils.gridx = 0;
        gbc_lbUtils.gridy = 2;
        contentPane.add(lbUtils, gbc_lbUtils);

        btShortcut = new JButton("Create a shortcut");
        btShortcut.setFont(mediumFont);
        final GridBagConstraints gbc_btShortcut = new GridBagConstraints();
        gbc_btShortcut.insets = new Insets(0, 0, 10, 0);
        gbc_btShortcut.fill = GridBagConstraints.BOTH;
        gbc_btShortcut.gridwidth = 2;
        gbc_btShortcut.gridx = 0;
        gbc_btShortcut.gridy = 3;
        contentPane.add(btShortcut, gbc_btShortcut);

        btImport = new JButton("Import a GLR Manager profile");
        btImport.setFont(mediumFont);
        final GridBagConstraints gbc_btImport = new GridBagConstraints();
        gbc_btImport.fill = GridBagConstraints.BOTH;
        gbc_btImport.gridwidth = 2;
        gbc_btImport.insets = new Insets(0, 0, 0, 0);
        gbc_btImport.gridx = 0;
        gbc_btImport.gridy = 4;
        contentPane.add(btImport, gbc_btImport);
    }

    public class Controller implements ActionListener{
        private final Listener listener;

        public Controller(Settings.Listener listener){
            this.listener = listener;
        }

        public void initialize(){
            SwingUtilities.invokeLater(() -> {
                btBrowse.addActionListener(this);
                btImport.addActionListener(this);
                btShortcut.addActionListener(this);

                dialog.setVisible(true);
            });
        }

        public void populate(File outputDir){
            SwingUtilities.invokeLater(() -> {
                tfOutputPath.setText(outputDir.getAbsolutePath());
                dialog.pack();
                dialog.setMinimumSize(dialog.getSize());
                dialog.setLocationRelativeTo(dialog.getRootPane());
            });
        }

        @Override
        public void actionPerformed(ActionEvent e){
            JComponent s = (JComponent) e.getSource();
            if(s == btBrowse){
                listener.setOutputFile(this);
            }else if(s == btImport){
                listener.importGLRMProfile(this); // IO short enough for EDT
            }else if(s == btShortcut){
                listener.createShortcut(this); // IO short enough for EDT
            }
        }
    }

    public interface Listener{
        void setOutputFile(Controller settings);

        void importGLRMProfile(Controller settings);

        void createShortcut(Controller settings);
    }
}
