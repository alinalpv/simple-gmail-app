package com.simple.app.controller;

import com.google.api.services.gmail.model.Thread;
import com.simple.app.model.MessageService;
import com.simple.app.model.ThreadService;
import com.simple.app.view.GmailAppView;
import javafx.util.Pair;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.stream.Collectors;

public class GmailWebAppController {
    private GmailAppView gmailAppView;
    private MessageService messageService;
    private ThreadService threadService;

    public GmailWebAppController(GmailAppView gmailAppView, MessageService messageService, ThreadService threadService) {
        this.gmailAppView = gmailAppView;
        this.messageService = messageService;
        this.threadService = threadService;

        this.gmailAppView.setBtnMarkAsReadActionListener(new BtnGoActionListener());
        this.gmailAppView.setBtnInboxActionListener(new BtnInboxActionListener());
        this.gmailAppView.setBtnNextActionListener(new BtnNextActionListener());
        this.gmailAppView.setBtnPrevActionListener(new BtnPrevActionListener());
        this.gmailAppView.setBtnUnreadActionListener(new BtnUnreadActionListener());
        this.gmailAppView.setSubjectListSelectionListener(new SubjectListSelectionListener());
    }

    class BtnGoActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedIndex = gmailAppView.getSelectedIndex();
            messageService.markMessageAsSeen(messageService.getMessageForThread(gmailAppView.getSubjectList().get(selectedIndex).getKey()).get());
            threadService.getUnreadEmailsAsThreadList();
        }
    }

    class BtnInboxActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                List<Pair<Thread, String>> pairList = threadService.getAllEmailsAsThreadList()
                        .stream()
                        .map(thread -> new Pair<>(thread, messageService.getSubjectForThread(thread).get()))
                        .collect(Collectors.toList());

                gmailAppView.setSubjectList(pairList);
                gmailAppView.displayList();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    class BtnNextActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                List<Pair<Thread, String>> pairList = threadService.getNextBatch()
                        .stream()
                        .map(thread -> new Pair<>(thread, messageService.getSubjectForThread(thread).get()))
                        .collect(Collectors.toList());

                gmailAppView.setSubjectList(pairList);
                gmailAppView.displayList();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    class BtnPrevActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                List<Pair<Thread, String>> pairList = threadService.getPreviousEmailsAsThreadList()
                        .stream()
                        .map(thread -> new Pair<>(thread, messageService.getSubjectForThread(thread).get()))
                        .collect(Collectors.toList());

                gmailAppView.setSubjectList(pairList);
                gmailAppView.displayList();

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    class BtnUnreadActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                List<Pair<Thread, String>> pairList = threadService.getUnreadEmailsAsThreadList()
                        .stream()
                        .map(thread -> new Pair<>(thread, messageService.getSubjectForThread(thread).get()))
                        .collect(Collectors.toList());

                gmailAppView.setSubjectList(pairList);
                gmailAppView.displayList();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    class SubjectListSelectionListener implements ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {

            int selectedIndex = gmailAppView.getSubjectJList().getSelectedIndex();
            gmailAppView.setSelectedIndex(selectedIndex);

            if (selectedIndex >= 0 && selectedIndex < gmailAppView.getSubjectList().size()) {
                String htmlContent = messageService.getHtmlContentForThread(gmailAppView.getSubjectList().get(selectedIndex).getKey());
                gmailAppView.loadEmail(htmlContent);
            }

        }
    }

}
