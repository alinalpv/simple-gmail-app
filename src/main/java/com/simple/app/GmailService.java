package com.simple.app;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListThreadsResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.ModifyMessageRequest;
import com.google.api.services.gmail.model.Thread;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class GmailService {
    private static final long MAX_RESULTS = 10;
    private static final String UNREAD = "UNREAD";
    private static final String INBOX = "INBOX";
    private static final String UNREAD_QUERY = "is:unread";
    private static final List<String> METADATA_HEADERS = Arrays.asList("subject", "from", "to", "date");
    private static final String USER = "me";

    private Gmail service;
    private Stack<String> tokens = new Stack<>();

    public GmailService(Gmail service) {
        this.service = service;
    }

    public Message getSubjectForThread(Thread thread) throws IOException {
        return service.users().messages()
                .get(USER, thread.getId())
                .setFormat("metadata")
                .setMetadataHeaders(METADATA_HEADERS)
                .execute();

    }

    public Message getMessageForThread(Thread thread) throws IOException {
        return service.users().messages()
                .get(USER, thread.getId())
                .setFormat("raw")
                .execute();
    }

    public ListThreadsResponse getUnreadEmailsAsThreadList() throws IOException {
        return service.users().threads()
                .list(USER)
                .setLabelIds(Arrays.asList(INBOX))
                .setQ(UNREAD_QUERY)
                .execute();

    }

    public ListThreadsResponse getAllEmailsAsThreadList() throws IOException {
        return service.users().threads()
                .list(USER)
                .setLabelIds(Arrays.asList(INBOX))
                .setMaxResults(MAX_RESULTS)
                .execute();
    }

    public ListThreadsResponse getPreviousEmailsAsThreadList() throws IOException {
        return service.users().threads()
                .list(USER)
                .setLabelIds(Arrays.asList(INBOX))
                .setPageToken(tokens.pop())
                .setMaxResults(MAX_RESULTS)
                .execute();

    }

    public ListThreadsResponse getNextBatch(ListThreadsResponse listThreadsResponse) throws IOException {
        tokens.push(listThreadsResponse.getNextPageToken());
        return service.users().threads()
                .list(USER)
                .setLabelIds(Arrays.asList(INBOX))
                .setPageToken(listThreadsResponse.getNextPageToken())
                .setMaxResults(MAX_RESULTS)
                .execute();
    }

    public void markMessageAsSeen(Message m) throws IOException {
        ModifyMessageRequest mods = new ModifyMessageRequest().setAddLabelIds(Collections.emptyList())
                .setRemoveLabelIds(Arrays.asList(UNREAD));
        service.users().messages()
                .modify(USER, m.getId(), mods)
                .execute();
    }

    public void trash(Thread t) throws IOException {
        service.users().messages().trash(USER, t.getId());

    }
}
