import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AiService {
  
  private apiUrl = 'http://localhost:8080/api/ai'; 

  constructor(private http: HttpClient) {}

  getAdvice(ticker: string): Observable<{ content: string }> {
    return this.http.get<{ content: string }>(`${this.apiUrl}/advice/${ticker}`, {
      withCredentials: true 
    });
  }
}