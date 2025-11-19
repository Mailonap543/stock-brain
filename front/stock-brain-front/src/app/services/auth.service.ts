import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/auth'; 
  private loggedIn = new BehaviorSubject<boolean>(false);

  constructor(private http: HttpClient) {
    // Se tiver um usuário salvo no navegador, consideramos logado
    if (localStorage.getItem('user')) {
      this.loggedIn.next(true);
    }
  }

  login(credentials: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/login`, credentials, {
      withCredentials: true // IMPORTANTE: Permite salvar o Cookie de Sessão do Java
    }).pipe(
      tap((response: any) => {
        this.loggedIn.next(true);
        localStorage.setItem('user', response.username || credentials.username);
      })
    );
  }

  register(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/register`, userData);
  }

  logout() {
    this.loggedIn.next(false);
    localStorage.removeItem('user');
    
  }

  isLoggedIn() {
    return this.loggedIn.asObservable();
  }
}