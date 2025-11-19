import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class StockService {
  // URL do seu Backend Java
  private apiUrl = 'http://localhost:8080/api/stocks';

  constructor(private http: HttpClient) {}

  // Chama o endpoint de análise (aquele que testamos no Postman)
  analyze(ticker: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/analyze/${ticker}`, { 
      withCredentials: true // Importante para passar o cookie de login
    });
  }
}