package plus.messenger.email;

import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.StringUtils;
import plus.auth.entities.AuthUser;
import plus.messenger.core.entities.Notification;
import plus.messenger.core.entities.NotificationTemplate;
import plus.messenger.core.services.AbstractNotifier;
import plus.messenger.core.services.ChannelService;
import plus.messenger.core.services.UserService;
import reactor.core.publisher.Mono;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

@Getter
@Setter
public class EmailNotifier<T extends Notification,V extends NotificationTemplate> extends AbstractNotifier<T,V> {

    private JavaMailSender javaMailSender;
    private MailProperties mailProperties;

    public EmailNotifier(ChannelService channelService, UserService userService) {
        super(channelService, userService);
    }

    public EmailNotifier(ChannelService channelService, UserService userService,JavaMailSender javaMailSender,MailProperties mailProperties) {
        super(channelService, userService);
        this.javaMailSender = javaMailSender;
        this.mailProperties = mailProperties;
    }

    protected void sendEmail(String subject,
                             String content,
                             String[] to,
                             String from) throws MessagingException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper h = new MimeMessageHelper(message);
        h.setFrom(from);
        h.setTo(to);
        h.setText(content, true);
        h.setSubject(subject);
        javaMailSender.send(message);
    }

    protected String renderer(String templateName,String template, Map<String,Object> data) throws IOException, TemplateException {
        return FreeMarkerTemplateUtils.processTemplateIntoString(new Template(templateName,template,null),data);
    }

    @Override
    protected Mono<Boolean> doSendNotification(T notification, V template, Collection<AuthUser> users) throws Exception {
        ArrayList<String> emails = new ArrayList<>(users.size());
        for(AuthUser user :users){
            if(StringUtils.hasText(user.getEmail()))
                emails.add(user.getEmail());
        }
        String[] emailArr = emails.toArray(new String[]{});
        sendEmail(template.getName(),renderer(template.getName(),
                template.getContent(),
                notification.getContent()),
                emailArr,mailProperties.getUsername());
        return Mono.just(true);
    }
}
