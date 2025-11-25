package org.acme;

import java.util.ArrayList;
import java.util.List;

public class SearchJogoResponse {
    public List<Jogo> Jogos = new ArrayList<>();
    public long TotalJogos;
    public int TotalPages;
    public boolean HasMore;
    public String NextPage;
}