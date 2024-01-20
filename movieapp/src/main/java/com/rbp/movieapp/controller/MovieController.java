package com.rbp.movieapp.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rbp.movieapp.exception.MoviesNotFound;
import com.rbp.movieapp.exception.SeatAlreadyBooked;
import com.rbp.movieapp.models.Movie;
import com.rbp.movieapp.models.Ticket;
import com.rbp.movieapp.models.User;
import com.rbp.movieapp.payload.request.LoginRequest;
import com.rbp.movieapp.repository.MovieRepository;
import com.rbp.movieapp.repository.TicketRepository;
import com.rbp.movieapp.repository.UserRepository;
import com.rbp.movieapp.security.services.MovieService;
import com.rbp.movieapp.security.services.UserDetailsImpl;
import com.rbp.movieapp.security.services.UserDetailsServiceImpl;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@SuppressWarnings("unused")
@RestController
@RequestMapping("/api/v1.0/moviebooking")
@OpenAPIDefinition(
        info = @Info(
                title = "Movie Application API",
                description = "This API provides endpoints for managing movies."
        )
)
@Slf4j
@CrossOrigin(origins = "http://localhost:4200/")
public class MovieController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private MovieService movieService;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private NewTopic topic;

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private MovieRepository movieRepository;

    @PutMapping("/{loginId}/forgot")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "reset password")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<String> changePassword(@RequestBody LoginRequest loginRequest, @PathVariable String loginId){
    	log.debug("forgot password endopoint accessed by "+loginRequest.getLoginId());
        Optional<User> user1 = userRepository.findByLoginId(loginId);
            User availableUser = user1.get();
            User updatedUser = new User(
                            loginId,
                    availableUser.getFirstName(),
                    availableUser.getLastName(),
                    availableUser.getEmail(),
                    availableUser.getContactNumber(),
                    passwordEncoder.encode(loginRequest.getPassword())
                    );
            updatedUser.set_id(availableUser.get_id());
            updatedUser.setRoles(availableUser.getRoles());
            userRepository.save(updatedUser);
            log.debug(loginRequest.getLoginId()+" has password changed successfully");
            return new ResponseEntity<>("users password changed successfully",HttpStatus.OK);
    }

    @GetMapping("/all")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "search all movies")
//    @PreAuthorize("hasRole('USER')or hasRole('ADMIN')")
    public ResponseEntity<List<Movie>> getAllMovies(){
        log.debug("here u can access all the available movies");
        List<Movie> movieList = movieService.getAllMovies();
        if(movieList.isEmpty()){
            log.debug("currently no movies are available");
            throw new MoviesNotFound("No Movies are available");
        }
        else{
            log.debug("listed the available movies");
            return new ResponseEntity<>(movieList, HttpStatus.OK);
        }
    }

    @GetMapping("/movies/search/{movieName}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "search movies by movie name")
//    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Movie>> getMovieByName(@PathVariable String movieName){
        log.debug("here search a movie by its name");
        List<Movie> movieList = movieService.getMovieByName(movieName);
        if(movieList.isEmpty()){
            log.debug("currently no movies are available");
            throw new MoviesNotFound("Movies Not Found");
        }
        else
            log.debug("listed the available movies with title:"+movieName);
            return new ResponseEntity<>(movieList,HttpStatus.OK);
    }

    @PostMapping("/{movieName}/book")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "book ticket")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> bookTickets(@RequestBody Ticket ticket, @PathVariable String movieName) {
        log.debug(ticket.getLoginId() + " entered to book tickets");
        
        // Validate input data
        validateInput(ticket);

        List<Ticket> allTickets = movieService.findSeats(movieName, ticket.getTheatreName());
        
        if (allTickets != null && !allTickets.isEmpty()) {
            for (Ticket each : allTickets) {
                for (int i = 0; i < ticket.getNoOfTickets(); i++) {
                    if (each.getSeatNumber().contains(ticket.getSeatNumber().get(i))) {
                        log.debug("Seat is already booked");
                        throw new SeatAlreadyBooked("Seat number " + ticket.getSeatNumber().get(i) + " is already booked");
                    }
                }
            }
        } else {
            log.debug("No tickets are there with moviename");
        }

        log.debug("Tickets theaterName is: " + movieName + " " + ticket.getTheatreName() + " ");

        log.info("Available tickets "
                + movieService.findAvailableTickets(movieName, ticket.getTheatreName()));

        if (movieService.findAvailableTickets(movieName, ticket.getTheatreName()).get(0).getNoOfTicketsAvailable() >=
                ticket.getNoOfTickets()) {

            log.info("Available tickets "
                    + movieService.findAvailableTickets(movieName, ticket.getTheatreName()).get(0).getNoOfTicketsAvailable());
            
            // Save the ticket and handle transactional behavior if necessary
            movieService.saveTicket(ticket);

            log.debug(ticket.getLoginId() + " booked " + ticket.getNoOfTickets() + " tickets");

            kafkaTemplate.send(topic.name(), "Movie ticket booked. Booking Details are: " + ticket);

            return new ResponseEntity<>("Tickets Booked Successfully with seat numbers" + ticket.getSeatNumber(), HttpStatus.OK);
        } else {
            log.debug("Tickets sold out");
            return new ResponseEntity<>("\"All tickets sold out\"", HttpStatus.OK);
        }
    }

    private void validateInput(Ticket ticket) {
    	 if (ticket.getNoOfTickets() < 0) {
    	        throw new IllegalArgumentException("Number of tickets must be non-negative");
    	    }

    	    // Check that seatNumber list is not null and not empty
    	    List<String> seatNumbers = ticket.getSeatNumber();
    	    if (seatNumbers == null || seatNumbers.isEmpty()) {
    	        throw new IllegalArgumentException("Seat number list must not be null or empty");
    	    }
    }


    @GetMapping("/getallbookedtickets/{movieName}")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "get all booked tickets(Admin Only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Ticket>> getAllBookedTickets(@PathVariable String movieName){
        return new ResponseEntity<>(movieService.getAllBookedTickets(movieName),HttpStatus.OK);
    }

    @PutMapping("/{movieName}/update/{ticketId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateTicketStatus(@PathVariable String movieName, @PathVariable ObjectId ticketId) {
        List<Movie> movie = movieRepository.findByMovieName(movieName);
        
        if (movie.isEmpty()) {
            throw new MoviesNotFound("Movie not found: " + movieName);
        }

        Movie movies = movie.get(0); // Assuming there's only one movie with the given name

        List<Ticket> ticket = ticketRepository.findBy_id(ticketId);
        
        if (ticket.isEmpty()) {
            throw new NoSuchElementException("Ticket not found:" + ticketId);
        }
        
        // Assuming you only update the status for the specific movie and ticket
        if (movies.getNoOfTicketsAvailable() == 0) {
            movies.setTicketsStatus("SOLD OUT");
        } else {
            movies.setTicketsStatus("BOOK ASAP");
        }

        // Assuming movieService has a saveMovie method
        movieService.saveMovie(movies);

        return new ResponseEntity<>("Ticket status updated successfully", HttpStatus.OK);
    }



    @DeleteMapping("/{movieName}/delete")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "delete a movie(Admin Only)")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> deleteMovie(@PathVariable String movieName){
        List<Movie> availableMovies = movieService.findByMovieName(movieName);
        if(availableMovies.isEmpty()){
            throw new MoviesNotFound("No movies Available with moviename "+ movieName);
        }
        else {
            movieService.deleteByMovieName(movieName);
            kafkaTemplate.send(topic.name(),"Movie Deleted by the Admin. "+movieName+" is now not available");
            return new ResponseEntity<>("Movie deleted successfully",HttpStatus.OK);
        }

    }




}
