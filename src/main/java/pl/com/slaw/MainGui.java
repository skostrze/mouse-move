package pl.com.slaw;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class MainGui extends JDialog {
    private JPanel contentPane;
    private JButton buttonExit;
    private JButton buttonAction;
    private String action = "Start";

    public static final int FIVE_SECONDS = 5000;
    public static final int MAX_Y = 400;
    public static final int MAX_X = 400;
    private final Robot robot;
    private final Random random;
    private Thread cursorThread;
    public MainGui() throws AWTException {
        setModal(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

        setContentPane(contentPane);
        getRootPane().setDefaultButton(buttonAction);

        buttonExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onExit();
            }
        });

        // call onCancel() when cross is clicked

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onExit();
            }
        });

        // call onCancel() on ESCAPE
        contentPane
                .registerKeyboardAction(e -> onExit(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                        JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        buttonAction.setText(action);


        buttonAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    onAction();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        robot = new Robot();
        random = new Random();
    }

    private void onAction() throws InterruptedException {
        if (action.equals("Start")) {
            action = "Stop";

            cursorThread = new Thread(() -> {
                try {
                    while (!Thread.currentThread().isInterrupted()) {
                        robot.mouseMove(random.nextInt(MAX_X), random.nextInt(MAX_Y));
                        Thread.sleep(FIVE_SECONDS);
                    }
                } catch (InterruptedException e) {
                    // Oczekiwane przerwanie wątku - bez reakcji
                    Thread.currentThread().interrupt(); // Upewnij się, że stan przerwania jest ustawiony
                }
            });
            cursorThread.start();
        } else {
            stopCursorThread();
            action = "Start";
        }
        buttonAction.setText(action);
    }
    private void onExit() {
        stopCursorThread();
        dispose();
    }



    private void stopCursorThread() {
        if (cursorThread != null && cursorThread.isAlive()) {
            cursorThread.interrupt(); // Przerwanie wątku
            try {
                cursorThread.join(); // Czekanie na zakończenie wątku
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public static void main(String[] args) throws AWTException {
        MainGui dialog = new MainGui();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
