package com.simple.app.model;

import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.google.api.services.gmail.model.Thread;
import com.simple.app.GmailService;
import org.apache.commons.mail.util.MimeMessageParser;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

public class MessageService {
    private GmailService service;
    private Base64 base64Url = new Base64(true);
    private Properties props = new Properties();
    private Session session = Session.getDefaultInstance(props, null);
    private HashMap<Thread, Message> messageMap = new HashMap<>();
    private HashMap<Thread, MimeMessage> mimeMessageMap = new HashMap<>();
    private HashMap<Thread, List<MessagePartHeader>> subjectMap = new HashMap<>();


    public MessageService(GmailService service) {
        this.service = service;
    }

    public Optional<String> getSubjectForThread(Thread thread) {
        if (subjectMap.containsKey(thread)) {
            return getSubjectFromHeaders(thread);
        }
        try {
            Message t = service.getSubjectForThread(thread);
            subjectMap.put(thread, t.getPayload().getHeaders());

            return getSubjectFromHeaders(thread);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    private Optional<String> getSubjectFromHeaders(Thread t) {
        return subjectMap.get(t).stream().filter(h -> h.getName().equals("Subject")).map(header -> header.getValue()).findFirst();
    }

    public String getHtmlContentForThread(Thread thread) {
        try {
            MimeMessage m = this.getMimeMessageForThread(thread).get();
            MimeMessageParser parser = new MimeMessageParser(m);
            return parser.parse().getHtmlContent();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return "";
    }

    public Optional<Message> getMessageForThread(Thread thread) {
        if (messageMap.containsKey(thread)) {
            return Optional.of(messageMap.get(thread));
        }
        try {
            Message t = service.getMessageForThread(thread);
            messageMap.put(thread, t);
            return Optional.of(t);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public Optional<MimeMessage> getMimeMessageForThread(Thread thread) {
        if (mimeMessageMap.containsKey(thread)) {
            return Optional.of(mimeMessageMap.get(thread));
        }
        try {
            Message t = getMessageForThread(thread).get();
            byte[] emailBytes = base64Url.decodeBase64(t.getRaw());

            MimeMessage mi = new MimeMessage(session, new ByteArrayInputStream(emailBytes));
            mimeMessageMap.put(thread, mi);
            return Optional.of(mi);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    public void markMessageAsSeen(Message m) {
        try {
            service.markMessageAsSeen(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
