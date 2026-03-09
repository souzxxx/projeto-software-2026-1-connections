package br.insper.conexoes.connections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ConnectionService {

    private final ConnectionRepository repository;

    @Autowired
    private UserClient userClient;

    public ConnectionService(ConnectionRepository repository) {
        this.repository = repository;
    }

    public Connection create(String fromUserId, String toUserId) {

        if (!userClient.userExists(fromUserId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        if (!userClient.userExists(toUserId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Connection connection = new Connection(fromUserId, toUserId);
        return repository.save(connection);
    }

    public List<Connection> listByUser(String userId) {
        return repository.findByFromUserId(userId);
    }

    public void delete(String fromUserId, String toUserId) {
        repository.deleteByFromUserIdAndToUserId(fromUserId, toUserId);
    }
}