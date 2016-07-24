package com.op.rentit.repository;

import com.op.rentit.domain.AppSettings;

import org.springframework.data.jpa.repository.*;

import java.util.List;

/**
 * Spring Data JPA repository for the AppSettings entity.
 */
@SuppressWarnings("unused")
public interface AppSettingsRepository extends JpaRepository<AppSettings,Long> {

}
