package it.unicam.cs.mpgc.rpg122627;

import it.unicam.cs.mpgc.rpg122627.persistence.ClasspathCaseRepository;
import it.unicam.cs.mpgc.rpg122627.persistence.FileSaveRepository;
import it.unicam.cs.mpgc.rpg122627.ui.MainWindow;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Punto di ingresso: collega le implementazioni concrete e apre la finestra. */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // si usa l'aspetto predefinito di Swing
            }
            new MainWindow(new ClasspathCaseRepository(),
                    new FileSaveRepository(FileSaveRepository.defaultDirectory())).open();
        });
    }
}
