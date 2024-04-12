package majornick;

import com.fasterxml.jackson.databind.ObjectMapper;
import discord4j.core.DiscordClient;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import majornick.models.CFResponse;
import majornick.models.Contest;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ContestReminderBot {
    private final String CF_CONTEST_LIST = "https://codeforces.com/api/contest.list?gym=false";

    public void start() {

        DiscordClient client = DiscordClient.create("{Token}");
        Mono<Void> login = client.withGateway((GatewayDiscordClient gateway) -> gateway.on(MessageCreateEvent.class, event -> {
            Message message = event.getMessage();

            if (message.getContent().equals("!dc")) {
                var k = getTodaysContest();
                return k.<org.reactivestreams.Publisher<Message>>map(contest -> message.getChannel().flatMap(ch -> ch.createMessage(contest.toString()))).orElseGet(() -> message.getChannel().flatMap(ch -> ch.createMessage("No Contest Today")));
            }
            return Mono.empty();
        }));
        login.block();
    }

    private Optional<Contest> getTodaysContest() {
        List<Contest> contests = getActiveContests();
        Contest todaysContest = null;
        for (Contest c : contests) {

            if (c.getPhase().equalsIgnoreCase("BEFORE")) {

                Instant instant = Instant.ofEpochSecond(Long.parseLong(c.getStartTimeSeconds()));


                LocalDateTime contestDate = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();
                if (LocalDateTime.now().getDayOfYear() == contestDate.getDayOfYear()) {
                    todaysContest = c;
                    break;
                }
            }
        }
        return Optional.ofNullable(todaysContest);
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
            return objectMapper.readValue(respBody, CFResponse.class).getResult();
        } catch (InterruptedException | IOException e) {

            System.err.println(e.getMessage());
        }
        return Collections.emptyList();
    }


}
