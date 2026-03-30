package br.insper.conexoes.connections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConnectionServiceTest {

    @Mock
    private ConnectionRepository repository;

    @Mock
    private EventProducer eventProducer;

    @Mock
    private UserClient userClient;

    @InjectMocks
    private ConnectionService connectionService;

    @Test
    void create_shouldSaveConnectionWhenBothUsersExist() {
        // mocks
        String fromUserId = "user1";
        String toUserId = "user2";

        when(userClient.userExists(fromUserId)).thenReturn(true);
        when(userClient.userExists(toUserId)).thenReturn(true);

        Connection savedConnection = new Connection(fromUserId, toUserId);
        when(repository.save(any(Connection.class))).thenReturn(savedConnection);

        // ação
        Connection result = connectionService.create(fromUserId, toUserId);

        // asserções
        assertNotNull(result);
        assertEquals(fromUserId, result.getFromUserId());
        assertEquals(toUserId, result.getToUserId());

        verify(userClient).userExists(fromUserId);
        verify(userClient).userExists(toUserId);
        verify(eventProducer).send(any(Event.class));
        verify(repository).save(any(Connection.class));
    }

    // createShouldThrowNotFoundWhenFromUserDoesNotExist
    @Test
    void create_shouldThrowNotFoundWhenFromUserDoesNotExist() {
        String fromUserId = "user1";
        String toUserId = "user2";

        when(userClient.userExists(fromUserId)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> {
            connectionService.create(fromUserId, toUserId);
        });

        verify(userClient).userExists(fromUserId);
        verify(userClient, never()).userExists(toUserId);
        verify(eventProducer, never()).send(any(Event.class));
        verify(repository, never()).save(any(Connection.class));
    }
    // createShouldThrowNotFoundWhenToUserDoesNotExist
    // listByUserShouldReturnConnectionsAndSendEvent
    // deleteShouldSendEventAndRemoveConnection

}
