package com.op.rentit.repository;

import com.op.rentit.domain.Booking;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the Booking entity.
 */
@SuppressWarnings("unused")
public interface BookingRepository extends JpaRepository<Booking,Long> {

    @Query("select booking from Booking booking where booking.user.login = ?#{principal.username}")
    List<Booking> findByUserIsCurrentUser();

}
