package com.criticalmass.core.services;

import java.util.ArrayList;
import com.criticalmass.core.models.EdgarTdpCardsModel;

/**
 * Service interface definition.
 * Interfaces are important in OSGi because multiple implementations can exist.
 */
public interface EdgarTdpService {
    ArrayList<EdgarTdpCardsModel> getCardInfo(String cardName);
}
