package org.acme;

import java.util.ArrayList;
import java.util.List;

public class SearchDesenvolvedoraResponse {
    public List<Desenvolvedora> Desenvolvedoras = new ArrayList<>();
    public long TotalDesenvolvedoras;
    public int TotalPages;
    public boolean HasMore;
    public String NextPage;
}