package org.example.tournamentapp.wrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.tournamentapp.model.PairDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
public class PairsWrapper {
    private List<PairDto> pairs;
}