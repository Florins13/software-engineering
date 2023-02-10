package org.uab.bike;

import io.quarkus.hibernate.orm.panache.PanacheQuery;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.mockito.InjectMock;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.uab.bike.model.Bike;
import org.uab.bike.repository.BikeRepository;
import org.uab.bike.service.BikeService;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@QuarkusTest
public class BikeServiceTest {

    @Inject
    BikeService bikeService;
    @InjectMock
    BikeRepository bikeRepository;

    @BeforeEach
    void setUp() {
        PanacheQuery query = Mockito.mock(PanacheQuery.class);
        List<Bike> bikes = new ArrayList<>();
        bikes.add(new Bike());
        bikes.get(0).setModel("test");
        bikes.get(0).setStock(5);
        Mockito.when(query.list()).thenReturn(bikes);
        when(bikeRepository.getAllBikes()).thenReturn(bikes);
    }
    @Test
    public void shouldReturnBikePageNoAuth() {
        Assertions.assertEquals(bikeService.getAllBikes().get(0).getModel(),"test");
    };

}
