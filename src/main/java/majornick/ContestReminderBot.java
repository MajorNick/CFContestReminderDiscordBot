package majornick;

import com.fasterxml.jackson.databind.ObjectMapper;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import majornick.models.Contest;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ContestReminderBot {
    private final String CF_CONTEST_LIST = "https://codeforces.com/api/contest.list?gym=false";

    public ContestReminderBot() {
    }

    public void start() {
        DiscordClient client = DiscordClient.create("{Token}");
        Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> gateway.on(MessageCreateEvent.class, event -> {
            Message message = event.getMessage();
            if (message.getContent().equals("!dc")) {
                return message.getChannel().flatMap(ch -> ch.createMessage("baro"));
            }
            return Mono.empty();
        }));
        login.block();
    }

    private List<Contest> getActiveContests() {
        HttpClient httpClient = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(CF_CONTEST_LIST))
                .GET()
                .build();
        try {
            String respBody = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
            ObjectMapper objectMapper = new ObjectMapper();
            return Arrays.asList(objectMapper.readValue(respBody, Contest[].class));
        } catch (InterruptedException | IOException e) {
            System.err.println(e.getMessage());
        }
        return Collections.emptyList();
    }


}
