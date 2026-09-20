package com.srcarcare.app.service;

import com.srcarcare.app.entity.Offer;
import com.srcarcare.app.repository.OfferRepository;
import com.srcarcare.app.util.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OfferService {

    private final OfferRepository offerRepository;

    public OfferService(OfferRepository offerRepository) {
        this.offerRepository = offerRepository;
    }

    public List<Offer> listAll() {
        return offerRepository.findAllByOrderByIdDesc();
    }

    public List<Offer> listCurrentlyRunning() {
        return offerRepository.findByActiveTrue().stream()
                .filter(Offer::isCurrentlyRunning)
                .collect(Collectors.toList());
    }

    public Offer getById(Long id) {
        return offerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Offer not found with id " + id));
    }

    public Offer save(Offer offer) {
        return offerRepository.save(offer);
    }

    public void delete(Long id) {
        offerRepository.deleteById(id);
    }
}
