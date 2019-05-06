package com.simple.app.view;

import com.google.api.services.gmail.model.Thread;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Pair;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

public class GmailAppView extends JFrame {
    private final JFXPanel jfxPanel = new JFXPanel();
    private WebEngine engine;
    private final JPanel panel = new JPanel(new BorderLayout());
    private final JLabel lblStatus = new JLabel();

    private final JButton btnMarkAsRead = new JButton("Set as read");
    private final JButton btnNext = new JButton("Next page");
    private final JButton btnPrev = new JButton("Previous page");

    private final JButton btnINBOX = new JButton("INBOX");
    private final JButton btnUNREAD = new JButton("UNREAD");
    private DefaultListModel<String> defaultListModel = new DefaultListModel<>();
    private final JList<String> emailsList = new JList<>(defaultListModel);
    private final JScrollPane listScrollPane = new JScrollPane(emailsList);
    private final JProgressBar progressBar = new JProgressBar();
    private List<Pair<Thread, String>> subject;
    private int selectedIndex = 0;

    public GmailAppView(){
        createScene();

        emailsList.setModel(defaultListModel);
        emailsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        progressBar.setPreferredSize(new Dimension(150, 18));
        progressBar.setStringPainted(true);

        JPanel leftBar = new JPanel(new GridLayout(3, 1, 5, 5));
        leftBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        leftBar.add(btnINBOX);
        leftBar.add(btnUNREAD);

        JPanel topLeft = new JPanel(new GridLayout(3, 1, 5, 0));
        topLeft.add(btnMarkAsRead);
        topLeft.add(btnNext);
        topLeft.add(btnPrev);

        JPanel topBar = new JPanel(new BorderLayout(5, 5));
        topBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        topBar.add(listScrollPane, BorderLayout.CENTER);
        topBar.add(topLeft, BorderLayout.EAST);
        topBar.add(leftBar, BorderLayout.WEST);


        JPanel statusBar = new JPanel(new BorderLayout(5, 0));
        statusBar.setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
        statusBar.add(lblStatus, BorderLayout.CENTER);
        statusBar.add(progressBar, BorderLayout.EAST);

        panel.add(topBar, BorderLayout.NORTH);
        panel.add(jfxPanel, BorderLayout.CENTER);
        panel.add(statusBar, BorderLayout.SOUTH);

        getContentPane().add(panel);


        setPreferredSize(new Dimension(1024, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
    }

    public void setBtnNextActionListener(ActionListener al) {
        btnNext.addActionListener(al);
    }

    public void setBtnMarkAsReadActionListener(ActionListener al) {
        btnMarkAsRead.addActionListener(al);
    }

    public void setBtnPrevActionListener(ActionListener al) {
        btnPrev.addActionListener(al);
    }

    public void setBtnInboxActionListener(ActionListener al) {
        btnINBOX.addActionListener(al);
    }

    public void setBtnUnreadActionListener(ActionListener al) {
        btnUNREAD.addActionListener(al);
    }

    public void setSubjectListSelectionListener(ListSelectionListener ls) {
        emailsList.addListSelectionListener(ls);
    }

    public void setSelectedIndex(int selectedIndex) {
        this.selectedIndex = selectedIndex;
    }

    public JList getSubjectJList() {
        return emailsList;
    }

    public List<Pair<Thread, String>> getSubjectList() {
        return subject;
    }

    public void setSubjectList(List<Pair<Thread, String>> subjectList) {
        this.subject = subjectList;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    public void loadEmail(String htmlContent) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    if (selectedIndex >= 0 && selectedIndex < subject.size()) {
                        engine.loadContent(htmlContent);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void displayList() {
        DefaultListModel<String> dlm = new DefaultListModel<>();
        selectedIndex = 0;
        subject.stream().forEach(msg -> {
            dlm.addElement(msg.getValue());
        });
        emailsList.setModel(dlm);
    }

    private void createScene() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                WebView view = new WebView();
                engine = view.getEngine();
                Scene scene = new Scene(view);
                jfxPanel.setScene(scene);

                engine.titleProperty().addListener(new ChangeListener<String>() {
                    @Override
                    public void changed(ObservableValue<? extends String> observable,
                                        String oldValue, final String newValue) {
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                GmailAppView.this.setTitle(newValue);
                            }
                        });
                    }
                });
            }
        });
    }
}
