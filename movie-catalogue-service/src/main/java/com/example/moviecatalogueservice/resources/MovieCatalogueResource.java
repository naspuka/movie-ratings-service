package com.example.moviecatalogueservice.resources;

import com.example.moviecatalogueservice.models.CatalogueItem;
import com.example.moviecatalogueservice.models.Movie;
import com.example.moviecatalogueservice.models.Rating;
import com.example.moviecatalogueservice.models.UserRating;
import com.netflix.discovery.DiscoveryClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalogue")
public class MovieCatalogueResource {

    @Autowired
    private RestTemplate restTemplate;

    private DiscoveryClient discoveryClient;

    @RequestMapping("/{userId}")
    public List<CatalogueItem> getCatalogue(@PathVariable("userId") String  userId){
        // get all rated movieId
        UserRating ratings = restTemplate.getForObject("http://ratings-data-service/ratingsdata/users/" + userId, UserRating.class);

        return ratings.getUserRating().stream().map(rating -> {
            // for each movieId, call movie info service and get detail
            Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);
            // put them all together
            return new CatalogueItem( movie.getName(), "Desc", rating.getRating());
            }).collect(Collectors.toList());


    }
}
