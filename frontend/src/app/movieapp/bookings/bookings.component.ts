import { Component, OnInit } from '@angular/core';
import { MovieService } from 'src/app/movie.service';
import { Ticket } from '../models/Ticket';

@Component({
  selector: 'app-bookings',
  templateUrl: './bookings.component.html',
  styleUrls: ['./bookings.component.css']
})
export class BookingsComponent implements OnInit {

  allTickets : Ticket[] = [];
  movieName : string = '';
  theatreName: string = '';
  isFormSubmit : boolean = false;

  constructor(private movieService:MovieService) { }
  
  getAllTickets(){
    this.movieName = this.movieService.validateMovieName(this.movieName);
    this.theatreName = this.movieService.validateMovieName(this.theatreName);
    this.isFormSubmit = true
    this.movieService.forGettingAllBookedTickets(this.movieName).subscribe(
      (result)=>{
        console.log(result);
        this.allTickets = result
      }
    ) 
  }

  ngOnInit(): void {
  }

}
