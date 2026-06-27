package org.acme.Repository;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import org.acme.Entity.SellerEntity;

@ApplicationScoped
public class SellerRepository implements PanacheRepository<SellerEntity> {

    public SellerEntity findByEmail(String email) {
        return find("email", email).firstResult();
    }

    public SellerEntity findByTag(String tag) {
        return find("tag", tag).firstResult();
    }
}
