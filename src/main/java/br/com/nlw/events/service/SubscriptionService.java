package br.com.nlw.events.service;

import java.util.List;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.nlw.events.dto.SubscriptionRankingByUser;
import br.com.nlw.events.dto.SubscriptionRankingItem;
import br.com.nlw.events.dto.SubscriptionResponse;
import br.com.nlw.events.exception.EventNotFoundException;
import br.com.nlw.events.exception.SubscriptionConflictException;
import br.com.nlw.events.exception.UserIndicadorNotFound;
import br.com.nlw.events.model.EventModel;
import br.com.nlw.events.model.Subscription;
import br.com.nlw.events.model.User;
import br.com.nlw.events.repo.EventRepo;
import br.com.nlw.events.repo.SubscriptionRepo;
import br.com.nlw.events.repo.UserRepo;

@Service
public class SubscriptionService {

    @Autowired
    private EventRepo evtRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private SubscriptionRepo subRepo;

    public SubscriptionResponse createNewSubscription(String eventName, User user, Integer userId) {
        
        EventModel event = evtRepo.findByPrettyName(eventName);
        if (event == null) {
            throw new EventNotFoundException("Evento "+eventName+" não existe.");
        }

        User userRec = userRepo.findByEmail(user.getEmail());
        if (userRec == null) {
            userRec = userRepo.save(user);
        }

        Subscription subscription = new Subscription();

        if (userId != null) {
            User indicador = userRepo.findById(userId).orElse(null);
            if (indicador == null) {
                throw new UserIndicadorNotFound("Usuário "+userId+" indicador não existe.");
            }
            subscription.setIndication(indicador);
        }
        
        subscription.setEvent(event);
        subscription.setSubscriber(userRec);
        
        Subscription tmpSub = subRepo.findByEventAndSubscriber(event, userRec);
        
        if (tmpSub != null) {
            throw new SubscriptionConflictException("Já existe inscrição para o usuário "+userRec.getName()+" no evento "+event.getTitle());
        }

        Subscription resultado = subRepo.save(subscription);
        return new SubscriptionResponse(resultado.getSubscriptionNumber(), "http://codigosfodas.com/"+resultado.getEvent().getPrettyName()+"/"+resultado.getSubscriber().getId());
    }

    public List<SubscriptionRankingItem> getCompleteRanking(String prettyName) {
        EventModel evt = evtRepo.findByPrettyName(prettyName);
        if (evt == null) {
            throw new EventNotFoundException("Ranking do evento "+prettyName+" não existe.");
        }
        return subRepo.generateRanking(evt.getEventId());
    };

    public SubscriptionRankingByUser getRankingByUser (String prettyName, Integer userId) {
        List<SubscriptionRankingItem> ranking = getCompleteRanking(prettyName);

        SubscriptionRankingItem item = ranking.stream().filter(r -> r.user_id().equals(userId)).findFirst().orElse(null);
        if (item == null) {
            throw new UserIndicadorNotFound("Não há inscrições com indicações do usuário "+userId+".");
        }
        
        Integer position = IntStream.range(0, ranking.size())
            .filter(i -> ranking.get(i).user_id().equals(userId))
            .findFirst()
            .getAsInt();

        return new SubscriptionRankingByUser(item, position+1);
    }
}
