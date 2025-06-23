package bagagem.main;

import bagagem.gui.MainFrame;
import bagagem.model.ProcessoRepository; // Importe o repositório, mesmo que só para limpar
import javax.swing.SwingUtilities;

public class Application {

    public static void main(String[] args) {
        // Remover ou comentar esta linha para que os dados persistam entre as execuções
        // ProcessoRepository.limparRepositorio(); 

        SwingUtilities.invokeLater(() -> {
            MainFrame mainFrame = new MainFrame();
            mainFrame.setVisible(true);
        });
    }
}