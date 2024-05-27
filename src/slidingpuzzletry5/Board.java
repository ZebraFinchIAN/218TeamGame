package slidingpuzzletry5;
import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Random;


public class Board implements ActionListener {
    JFrame fr;
    JPanel mainPanel;
    JButton[][] button;
    int rows;
    int cols;
    JLabel[][] label;
    int[][] board;
    String imagePrefix;
    Clip error;
    Clip backgroundMusic;

    public Board(String imagePrefix, int rows, int cols) {
        this.imagePrefix = imagePrefix;
        this.rows = rows;
        this.cols = cols;
        board = new int[rows][cols];
        initGUI();
        loadErrorSound();
        
    }


    public void initGUI() {
        fr = new JFrame("Sliding Puzzle Game");
        fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Calculate the total width and height needed for the images and borders
        int totalWidth = cols * 150 + (cols - 1) * 2; // Width of images + border widths
        int totalHeight = rows * 150 + (rows - 1) * 2; // Height of images + border heights

        // Adjust the frame size based on the total width and height
        fr.setSize(totalWidth, totalHeight);

        mainPanel = new JPanel();
        mainPanel.setBackground(Color.white);
        mainPanel.setLayout(new GridLayout(rows, cols));
        button = new JButton[rows][cols];
        label = new JLabel[rows][cols];

        // Call the shuffle board
        this.shuffleBoard();

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                button[i][j] = new JButton();
                button[i][j].setText(i + "," + j);
                button[i][j].setFont(new Font("TimesRoman", Font.PLAIN, 0));
                button[i][j].addActionListener(this);

                int val = board[i][j];
                if (val != -1) {
                    String fileName = imagePrefix + "/" + val + ".jpg";
                    ImageIcon icon = new ImageIcon(fileName);
                    Image img = icon.getImage();
                    Image newimg = img.getScaledInstance(150, 150, java.awt.Image.SCALE_SMOOTH); // Adjust the scaling as needed
                    icon = new ImageIcon(newimg);
                    label[i][j] = new JLabel(icon);
                } else {
                    label[i][j] = new JLabel("");
                }

                button[i][j].add(label[i][j]);
                button[i][j].setBorder(BorderFactory.createLineBorder(Color.black, 2));
                button[i][j].setBackground(Color.LIGHT_GRAY);
                mainPanel.add(button[i][j]);
            }
        }

        fr.add(mainPanel);
        fr.setVisible(true);
    
    }

    private void loadErrorSound() {
        try {
            // Specify the full path to the error.wav file
            String fullPath = "C:\\Users\\Merve\\Downloads\\error.wav"; // Replace with the actual path
            File soundFile = new File(fullPath);
            System.out.println("Looking for sound file at: " + soundFile.getAbsolutePath());
            if (!soundFile.exists()) {
                throw new IOException("Sound file not found: " + soundFile.getAbsolutePath());
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            error = AudioSystem.getClip();
            error.open(audioIn);
        } catch (UnsupportedAudioFileException e) {
            System.err.println("Unsupported audio file format: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error loading audio file: " + e.getMessage());
        } catch (LineUnavailableException e) {
            System.err.println("Audio line unavailable: " + e.getMessage());
        }
    }

    public void shuffleBoard() {
        Random rand = new Random();
        int[] array = new int[rows * cols];
        for (int i = 0; i < rows * cols; i++) {
            array[i] = i + 1;
        }
        array[rows * cols - 1] = -1; // Set last value as -1

        // Shuffle array
        for (int i = 0; i < rows * cols; i++) {
            int index = rand.nextInt(rows * cols);
            int temp = array[i];
            array[i] = array[index];
            array[index] = temp;
        }

        // Save it into board
        int count = 0;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                board[i][j] = array[count];
                count++;
            }
        }
    }

    Boolean isWin() {
        int count = 1;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] != count && board[i][j] != -1) {
                    return false;
                }
                count++;
            }
        }
        return true;
    }

    public void displayWinMsg() {
        JFrame frame = new JFrame("Game Win");
        JLabel label = new JLabel("You Solve The Puzzle", JLabel.CENTER);
        label.setFont(new Font("TimesRoman", Font.BOLD, 20));
        frame.add(label);
        frame.setLayout(new GridLayout(1, 1));
        frame.setSize(300, 300);
        frame.setBackground(Color.white);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        Boolean flag = isWin();
        if (!flag) {
            String s = ae.getActionCommand();
            int r = Integer.parseInt(s.split(",")[0]);
            int c = Integer.parseInt(s.split(",")[1]);

            if (board[r][c] != -1) {
                if (r + 1 < rows && board[r + 1][c] == -1) { // Move down
                    swap(r, c, r + 1, c);
                } else if (r - 1 >= 0 && board[r - 1][c] == -1) { // Move up
                    swap(r, c, r - 1, c);
                } else if (c + 1 < cols && board[r][c + 1] == -1) { // Move right
                    swap(r, c, r, c + 1);
                } else if (c - 1 >= 0 && board[r][c - 1] == -1) { // Move left
                    swap(r, c, r, c - 1);
                } else {
                    playErrorSound(); // Play error sound for invalid move
                }
            }

            flag = isWin();
            if (flag) {
                displayWinMsg();
            }
        }
    }

    private void swap(int r1, int c1, int r2, int c2) {
        label[r1][c1].setIcon(new ImageIcon(""));
        String fileName = imagePrefix + "/" + board[r1][c1] + ".jpg";
        ImageIcon icon = new ImageIcon(fileName);
        Image img = icon.getImage();
        Image newimg = img.getScaledInstance(150, 150, java.awt.Image.SCALE_SMOOTH); // Adjust the scaling as needed
        icon = new ImageIcon(newimg);
        label[r2][c2].setIcon(icon);

        int temp = board[r1][c1];
        board[r1][c1] = board[r2][c2];
        board[r2][c2] = temp;
    }

    private void playErrorSound() {
        if (error != null) {
            error.setFramePosition(0); // Rewind to the beginning
            error.start();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame welcomeFrame = new JFrame("Welcome to Sliding Puzzle Game!");
            welcomeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            welcomeFrame.setSize(400, 300);
            welcomeFrame.setLayout(new GridLayout(6, 1)); // Changed to 6 rows and 1 column

            JLabel welcomeLabel = new JLabel("Choose your puzzle", JLabel.CENTER);
            welcomeLabel.setFont(new Font("TimesRoman", Font.BOLD, 20));
            welcomeFrame.add(welcomeLabel);

            JButton buttonA = new JButton("Puzzle Number (Easy 3x3)");
            buttonA.addActionListener(e -> {
                welcomeFrame.dispose();
                new Board("backup pics 2", 3, 3); // 3x3 grid
            });

            JButton buttonB = new JButton("Puzzle Image (Easy 3x3)");
            buttonB.addActionListener(e -> {
                welcomeFrame.dispose();
                new Board("mlisa", 3, 3); // 3x3 grid
            });

            JButton buttonC = new JButton("Puzzle Number (Hard 4x4)");
            buttonC.addActionListener(e -> {
                welcomeFrame.dispose();
                new Board("backup pics 2", 4, 4); // 4x4 grid
            });

            JButton buttonD = new JButton("Puzzle Image (Hard 4x4)");
            buttonD.addActionListener(e -> {
                welcomeFrame.dispose();
                new Board("cato", 4, 4); // 4x4 grid
            });

            JButton creditsButton = new JButton("Credits");
            creditsButton.addActionListener(e -> displayCredits());

            welcomeFrame.add(buttonA);
            welcomeFrame.add(buttonB);
            welcomeFrame.add(buttonC);
            welcomeFrame.add(buttonD);
            welcomeFrame.add(creditsButton);

            welcomeFrame.setLocationRelativeTo(null);
            welcomeFrame.setVisible(true);
        });
    }

    private static void displayCredits() {
        JFrame creditsFrame = new JFrame("Credits");
        creditsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        creditsFrame.setSize(400, 300);
        creditsFrame.setLayout(new GridLayout(3, 1));

        addCredit(creditsFrame, "Author 1: Merve Bozdağ", "C:\\Users\\Merve\\Downloads\\MerveCat.jpg");
        addCredit(creditsFrame, "Author 2: Harun Tan", "C:\\Users\\Merve\\Downloads\\HarunCat.jpg");
        addCredit(creditsFrame, "Author 3: Baran Erol", "C:\\Users\\Merve\\Downloads\\BaranCat.jpg");

        creditsFrame.setLocationRelativeTo(null);
        creditsFrame.setVisible(true);
    }

    private static void addCredit(JFrame frame, String authorName, String imagePath) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel nameLabel = new JLabel(authorName, JLabel.CENTER);
        nameLabel.setFont(new Font("TimesRoman", Font.BOLD, 16));

        ImageIcon icon = new ImageIcon(imagePath);
        JLabel imageLabel = new JLabel(icon);

        panel.add(nameLabel, BorderLayout.NORTH);
        panel.add(imageLabel, BorderLayout.CENTER);

        frame.add(panel);
    }
}
// With the help of Javatpoint's source code we generated this game!
//https://www.javatpoint.com/Puzzle-Game
