import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.Image;
import java.awt.image.BufferedImage;

abstract class FeedbackButton extends JButton implements ActionListener {
    protected FeedbackGUI feedbackGUI;
    protected JLabel countLabel;
    protected String label;
    protected String emoji;
    protected Color color;

    public FeedbackButton(FeedbackGUI feedbackGUI, JLabel countLabel, String label, String emoji, Color color) {
        this.feedbackGUI = feedbackGUI;
        this.countLabel = countLabel;
        this.label = label;
        this.emoji = emoji;
        this.color = color;
        setupButton();
    }

    private void setupButton() {
        setFont(new Font("Segoe UI Emoji", Font.BOLD, 30));
        setBackground(color);
        setForeground(Color.WHITE);
        setFocusPainted(false);
        setPreferredSize(new Dimension(250, 250));
        setOpaque(true);
        setBorderPainted(false);
        setBorder(BorderFactory.createLineBorder(color.darker(), 5, true));

        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weighty = 0.6;
        gbc.anchor = GridBagConstraints.CENTER;
        JLabel labelEmoji = new JLabel(emoji, SwingConstants.CENTER);
        labelEmoji.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 80));
        labelEmoji.setForeground(Color.WHITE);
        add(labelEmoji, gbc);

        gbc.gridy = 1;
        gbc.weighty = 0.6;
        JLabel labelText = new JLabel(label, SwingConstants.CENTER);
        labelText.setFont(new Font("Segoe UI", Font.BOLD, 40));
        labelText.setForeground(Color.WHITE);
        add(labelText, gbc);

        addActionListener(this);
    }

    protected void updateCount() {
        feedbackGUI.incrementCount(label);
        feedbackGUI.updateBackgroundColor();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        try {
            JTextArea feedbackTextArea = new JTextArea(5, 30);
            feedbackTextArea.setLineWrap(true);
            feedbackTextArea.setWrapStyleWord(true);
            feedbackTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            JScrollPane scrollPane = new JScrollPane(feedbackTextArea);

            int option = JOptionPane.showConfirmDialog(feedbackGUI, scrollPane, "Nos forneça um feedback aqui: ", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                String feedbackComment = feedbackTextArea.getText();
                if (feedbackComment != null && !feedbackComment.trim().isEmpty()) {
                    feedbackGUI.addFeedbackComment(feedbackComment);
                    updateCount();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

class BomButton extends FeedbackButton {
    public BomButton(FeedbackGUI feedbackGUI, JLabel countLabel) {
        super(feedbackGUI, countLabel, "Bom", "😊", Color.decode("#66BB6A"));
    }
}

class MedioButton extends FeedbackButton {
    public MedioButton(FeedbackGUI feedbackGUI, JLabel countLabel) {
        super(feedbackGUI, countLabel, "Médio", "😐", Color.decode("#FFEB3B"));
    }
}

class RuimButton extends FeedbackButton {
    public RuimButton(FeedbackGUI feedbackGUI, JLabel countLabel) {
        super(feedbackGUI, countLabel, "Ruim", "😞", Color.decode("#EF5350"));
    }
}

class FeedbackCounts implements Serializable {
    private static final long serialVersionUID = 1L;
    private int bomCount;
    private int medioCount;
    private int ruimCount;

    public FeedbackCounts() {
        this.bomCount = 0;
        this.medioCount = 0;
        this.ruimCount = 0;
    }

    public int getBomCount() {
        return bomCount;
    }

    public void setBomCount(int bomCount) {
        this.bomCount = bomCount;
    }

    public int getMedioCount() {
        return medioCount;
    }

    public void setMedioCount(int medioCount) {
        this.medioCount = medioCount;
    }

    public int getRuimCount() {
        return ruimCount;
    }

    public void setRuimCount(int ruimCount) {
        this.ruimCount = ruimCount;
    }
}

class FeedbackComments implements Serializable {
    private static final long serialVersionUID = 1L;
    private List<String> comments;

    public FeedbackComments() {
        this.comments = new ArrayList<>();
    }

    public List<String> getComments() {
        return comments;
    }

    public void addComment(String comment) {
        comments.add(comment);
    }
}

public class FeedbackGUI extends JFrame {
    private JLabel bomLabel;
    private JLabel medioLabel;
    private JLabel ruimLabel;
    private int bomCount;
    private int medioCount;
    private int ruimCount;
    private JPanel panel;
    private FeedbackCounts feedbackCounts;
    private FeedbackComments feedbackComments;

    public FeedbackGUI() {
        setTitle("Sistema de Feedback");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        panel = new JPanel(new GridLayout(2, 3, 20, 20));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        bomLabel = new JLabel("Bom: " + bomCount, SwingConstants.CENTER);
        medioLabel = new JLabel("Médio: " + medioCount, SwingConstants.CENTER);
        ruimLabel = new JLabel("Ruim: " + ruimCount, SwingConstants.CENTER);

        bomLabel.setFont(new Font("Segoe UI", Font.BOLD, 40));
        medioLabel.setFont(new Font("Segoe UI", Font.BOLD, 40));
        ruimLabel.setFont(new Font("Segoe UI", Font.BOLD, 40));

        BomButton bomButton = new BomButton(this, bomLabel);
        MedioButton medioButton = new MedioButton(this, medioLabel);
        RuimButton ruimButton = new RuimButton(this, ruimLabel);

        panel.add(bomButton);
        panel.add(medioButton);
        panel.add(ruimButton);
        panel.add(bomLabel);
        panel.add(medioLabel);
        panel.add(ruimLabel);

        add(panel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton qrButton = new JButton("Mostrar QR Code");
        qrButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        qrButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showQRCode();
            }
        });
        buttonPanel.add(qrButton);

        JButton exportButton = new JButton("Exportar Feedbacks para CSV");
        exportButton.setFont(new Font("Segoe UI", Font.BOLD, 20));
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportFeedbacksToCSV();
            }
        });
        buttonPanel.add(exportButton);

        add(buttonPanel, BorderLayout.NORTH);

        feedbackComments = loadFeedbackComments();
        feedbackCounts = loadFeedbackCounts();

        bomCount = feedbackCounts.getBomCount();
        medioCount = feedbackCounts.getMedioCount();
        ruimCount = feedbackCounts.getRuimCount();

        bomLabel.setText("Bom: " + bomCount);
        medioLabel.setText("Médio: " + medioCount);
        ruimLabel.setText("Ruim: " + ruimCount);

        updateBackgroundColor();
    }

    public void incrementCount(String feedbackType) {
        if (feedbackType.equals("Bom")) {
            bomCount++;
            feedbackCounts.setBomCount(bomCount);
            bomLabel.setText("Bom: " + bomCount);
        } else if (feedbackType.equals("Médio")) {
            medioCount++;
            feedbackCounts.setMedioCount(medioCount);
            medioLabel.setText("Médio: " + medioCount);
        } else if (feedbackType.equals("Ruim")) {
            ruimCount++;
            feedbackCounts.setRuimCount(ruimCount);
            ruimLabel.setText("Ruim: " + ruimCount);
        }
        saveFeedbackCounts();
    }

    public void updateBackgroundColor() {
        if (bomCount > medioCount && bomCount > ruimCount) {
            panel.setBackground(Color.decode("#A5D6A7")); // Verde claro
        } else if (medioCount > bomCount && medioCount > ruimCount) {
            panel.setBackground(Color.decode("#FFF59D")); // Amarelo claro
        } else if (ruimCount > bomCount && ruimCount > medioCount) {
            panel.setBackground(Color.decode("#FFCDD2")); // Vermelho claro
        } else {
            panel.setBackground(Color.WHITE);
        }
    }

    public void addFeedbackComment(String comment) {
        feedbackComments.addComment(comment);
        saveFeedbackComments();
    }

    public void showQRCode() {
        try {
            BufferedImage originalImage = ImageIO.read(getClass().getResource("qrcode.png"));
            int maxWidth = 400;
            int maxHeight = 400;
            int newWidth = originalImage.getWidth();
            int newHeight = originalImage.getHeight();
            if (newWidth > maxWidth || newHeight > maxHeight) {
                double aspectRatio = (double) newWidth / newHeight;
                if (newWidth > maxWidth) {
                    newWidth = maxWidth;
                    newHeight = (int) (newWidth / aspectRatio);
                } else if (newHeight > maxHeight) {
                    newHeight = maxHeight;
                    newWidth = (int) (newHeight * aspectRatio);
                }
            }

            Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImage);

            JLabel qrLabel = new JLabel(scaledIcon);
            JOptionPane.showMessageDialog(this, qrLabel, "QR Code", JOptionPane.PLAIN_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void exportFeedbacksToCSV() {
        try {
            FileWriter writer = new FileWriter("feedbacks.csv");
            BufferedWriter bufferedWriter = new BufferedWriter(writer);

            bufferedWriter.write("Feedback,Comment\n");
            for (String comment : feedbackComments.getComments()) {
                bufferedWriter.write("Comentário," + comment + "\n");
            }

            bufferedWriter.close();
            JOptionPane.showMessageDialog(this, "Feedbacks exportados com sucesso!", "Exportação", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FeedbackCounts loadFeedbackCounts() {
        try {
            FileInputStream fileInputStream = new FileInputStream("feedbackCounts.ser");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            return (FeedbackCounts) objectInputStream.readObject();
        } catch (Exception e) {
            return new FeedbackCounts();
        }
    }

    public FeedbackComments loadFeedbackComments() {
        try {
            FileInputStream fileInputStream = new FileInputStream("feedbackComments.ser");
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
            return (FeedbackComments) objectInputStream.readObject();
        } catch (Exception e) {
            return new FeedbackComments();
        }
    }

    public void saveFeedbackCounts() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("feedbackCounts.ser");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(feedbackCounts);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFeedbackComments() {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("feedbackComments.ser");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(feedbackComments);
            objectOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FeedbackGUI().setVisible(true);
            }
        });
    }
}
