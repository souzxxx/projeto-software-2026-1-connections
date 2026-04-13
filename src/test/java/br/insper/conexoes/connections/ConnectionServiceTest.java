package br.insper.conexoes.connections;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

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

        //mock
        String fromUserId = "user1";
        String toUserId = "user2";
        when(userClient.userExists(fromUserId)).thenReturn(false);

        assertThrows(
                ResponseStatusException.class,
                () -> connectionService.create(fromUserId, toUserId)
        );

    }



    // createShouldThrowNotFoundWhenToUserDoesNotExist
    // listByUserShouldReturnConnectionsAndSendEvent
    @Test
    void list_shouldReturnConnectionsAndSendEvent() {

        Connection connection1 = new Connection("user-1", "user-2");
        Connection connection2 = new Connection("user-1", "user-3");

        List<Connection> lista = new ArrayList<>();
        lista.add(connection1);
        lista.add(connection2);

        when(repository.findByFromUserId("user-1"))
                .thenReturn(lista);

        // ação
        List<Connection> response = connectionService.listByUser("user-1");

        assertNotNull(response);
        assertEquals(2, response.size());
        assertEquals("user-1", response.getFirst().getFromUserId());

    }
    // deleteShouldSendEventAndRemoveConnection

}
