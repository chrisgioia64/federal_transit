package com.federal.dao;

import com.federal.model.AggregateStatistic;
import com.federal.model.TransitAggregateType;
import com.federal.model.web.MetroRankInfo;
import com.federal.model.web.TravelModeStatisticDatum;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.List;

public class MetroSummaryDaoImpl implements MetroSummaryDao {

    private DataSource dataSource;
    private JdbcTemplate template;

    private MetroRankDao metroRankDao;

    public MetroSummaryDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        this.template = new JdbcTemplate(dataSource);
        this.metroRankDao = new MetroRankDaoImpl(dataSource);
    }

    @Override
    public String getSummary(String metroName) {
//        MetroRankInfo pop = metroRankDao.getRankInfo(metroName, AggregateStatistic.POPULATION);
        MetroRankInfo upt = metroRankDao.getRankInfo(metroName, AggregateStatistic.UPT);
        MetroRankInfo operating = metroRankDao.getRankInfo(metroName, AggregateStatistic.OPERATING_EXPENSES);

        StringBuilder b = new StringBuilder();
        b.append(metroName + " has a population of " + upt.getPopulation() + " which ranks of " + upt.getPopulationRank() + " out of all metropolitan areas. ");
        b.append("It has " + upt.getTotalAmount() + " total unlinked passenger trips which ranks " + upt.getTotalRank() + " out of all metropolitan areas. ");
        b.append("The unlinked passenger trips per person is " + upt.getPerCapitaAmount() + " which ranks " + upt.getPerCapitaRank() + " out of all metropolitan areas. ");
        b.append("It has an operating expense of " + operating.getTotalAmount() + " which ranks " + operating.getTotalRank() + " out of all metropolitan areas. ");
        b.append("The operating expense per person is " + operating.getPerCapitaAmount() + " which ranks " + operating.getPerCapitaRank() + " out of all metropolitan areas. ");
        return b.toString();
    }

    @Override
    public String getSummary() {
        List<String> metropolitanAreas = metroRankDao.getMetropolitanAreas();
        StringBuilder b = new StringBuilder();
        for (String metropolitanArea : metropolitanAreas) {
            System.out.println("Metro: " + metropolitanArea);
            try {
                String summary = getSummary(metropolitanArea);
                b.append(summary);
            } catch (EmptyResultDataAccessException ex) {
                // pass
                System.out.println("  There was an error with " + metropolitanArea);
            }
            b.append("\n\n");
        }
        return b.toString();
    }

    @Override
    public String getUptUsageSummary(String metroName) {
        MetroRankInfo upt = metroRankDao.getRankInfo(metroName, AggregateStatistic.UPT);
        List<TravelModeStatisticDatum> datums = metroRankDao.getTravelModeStatisticDatums(metroName, AggregateStatistic.UPT);
        MetroRankInfo rail = metroRankDao.getTransitInfo(metroName, AggregateStatistic.UPT, TransitAggregateType.RAIL);
        MetroRankInfo bus = metroRankDao.getTransitInfo(metroName, AggregateStatistic.UPT, TransitAggregateType.BUS);
        double railAmount = metroRankDao.getAggregateAmount(metroName, AggregateStatistic.UPT, TransitAggregateType.RAIL);
        double busAmount = metroRankDao.getAggregateAmount(metroName, AggregateStatistic.UPT, TransitAggregateType.BUS);
        double demandAmount = metroRankDao.getAggregateAmount(metroName, AggregateStatistic.UPT, TransitAggregateType.DEMAND);
        String railPercent = String.format("%.1f", railAmount / (railAmount + busAmount + demandAmount) * 100);
        String busPercent = String.format("%.1f", busAmount / (railAmount + busAmount + demandAmount) * 100);


        StringBuilder b = new StringBuilder();
        b.append(metroName + " has a population of " + upt.getPopulation() + " which ranks of " + upt.getPopulationRank() + " out of all metropolitan areas. ");
        b.append("It has " + upt.getTotalAmount() + " total unlinked passenger trips which ranks " + upt.getTotalRank() + " out of all metropolitan areas. ");
        b.append("The unlinked passenger trips per person is " + upt.getPerCapitaAmount() + " which ranks " + upt.getPerCapitaRank() + " out of all metropolitan areas. ");

        for (TravelModeStatisticDatum datum : datums) {
            b.append("The agency " + datum.getAgencyName() + " for transit type " + datum.getTravelMode() + " has a ridership of " + datum.getAmount() + ". ");
        }
        b.append("The unlinked passenger trips per person for RAIL is " + rail.getPerCapitaAmount() + " which ranks " + rail.getPerCapitaRank() + " out of all metropolitan areas. ");
        b.append("The unlinked passenger trips per person for BUS is " + bus.getPerCapitaAmount() + " which ranks " + bus.getPerCapitaRank() + " out of all metropolitan areas. ");
        b.append("The rail usage is " + railPercent + "% of total usage. ");
        b.append("The bus usage is " + busPercent + "% of total usage. ");

        return b.toString();
    }

    @Override
    public String getUptUsageSummary() {
        List<String> metropolitanAreas = metroRankDao.getMetropolitanAreas();
        StringBuilder b = new StringBuilder();
        for (String metropolitanArea : metropolitanAreas) {
            System.out.println("Metro: " + metropolitanArea);
            try {
                String summary = getUptUsageSummary(metropolitanArea);
                b.append(summary);
            } catch (EmptyResultDataAccessException ex) {
                // pass
                System.out.println("  There was an error with " + metropolitanArea);
            }
            b.append("\n\n");
        }
        return b.toString();
    }
}
