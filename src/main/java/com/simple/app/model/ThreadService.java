package com.simple.app.model;

import com.google.api.services.gmail.model.ListThreadsResponse;
import com.google.api.services.gmail.model.Thread;
import com.simple.app.GmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class ThreadService {
    private static Logger LOG = LoggerFactory.getLogger(ThreadService.class);
    private GmailService service;
    private ListThreadsResponse listThreadsResponse;
    private Stack<String> tokens = new Stack<>();


    public ThreadService(GmailService service) {
        this.service = service;
    }

    public List<Thread> getUnreadEmailsAsThreadList() {
        List<Thread> unread = new ArrayList<>();
        try {
            unread = service.getUnreadEmailsAsThreadList().getThreads();
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return unread;
    }

    public List<Thread> getAllEmailsAsThreadList() {
        List<Thread> all = new ArrayList<>();
        try {
            listThreadsResponse = service.getAllEmailsAsThreadList();
            all = listThreadsResponse.getThreads();
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return all;
    }

    public List<Thread> getPreviousEmailsAsThreadList() {
        List<Thread> all = new ArrayList<>();
        try {
            if (!tokens.isEmpty()) {
                listThreadsResponse = service.getPreviousEmailsAsThreadList();
                all = listThreadsResponse.getThreads();
            } else {
                return getAllEmailsAsThreadList();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }

        return all;
    }

    public List<Thread> getNextBatch() {
        List<Thread> all = new ArrayList<>();
        try {
            if (listThreadsResponse != null) {
                tokens.push(listThreadsResponse.getNextPageToken());
                listThreadsResponse = service.getNextBatch(listThreadsResponse);
                all = listThreadsResponse.getThreads();
            }
        } catch (Exception e) {
            LOG.error(e.getMessage());
        }
        return all;
    }


    public void trash(Thread t) {
        try {
            service.trash(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
