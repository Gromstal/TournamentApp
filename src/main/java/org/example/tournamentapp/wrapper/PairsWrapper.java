package org.example.tournamentapp.wrapper;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.tournamentapp.model.PairDto;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PairsWrapper {
    @NotNull
    @Size(min = 1, message = "Нужна хотя бы одна пара")
    @Valid
    private List<PairDto> pairs;
}