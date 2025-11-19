import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { StockService } from '../../services/stock.service';
import { AiService } from '../../services/ai.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.html',
  styleUrls: ['./dashboard.scss']
})
export class DashboardComponent implements OnInit {
  ticker: string = '';
  stockData: any = null;
  aiAdvice: string = '';
  
  isLoadingStock: boolean = false;
  isLoadingAi: boolean = false;
  error: string = '';

  username: string = 'Investidor';
  avatarUrl: string = 'assets/perfil.jpg'; 

  constructor(
    private stockService: StockService,
    private aiService: AiService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    const storedUser = localStorage.getItem('user');
    if (storedUser) this.username = storedUser;

   
    const savedAvatar = localStorage.getItem('userAvatar');
    if (savedAvatar) {
      this.avatarUrl = savedAvatar;
    }
  }

  
  triggerFileInput() {
    document.getElementById('avatarInput')?.click();
  }

  onFileSelected(event: any) {
    const file = event.target.files[0];
    if (file) {
      const reader = new FileReader();
      reader.onload = (e: any) => {
        this.avatarUrl = e.target.result; 
        localStorage.setItem('userAvatar', this.avatarUrl); 
      };
      reader.readAsDataURL(file);
    }
  }
 

  analyze() {
    if (!this.ticker) return;
    this.isLoadingStock = true;
    this.stockData = null;
    this.aiAdvice = '';
    this.error = '';

    this.stockService.analyze(this.ticker).subscribe({
      next: (data) => {
        this.stockData = data;
        this.isLoadingStock = false;
      },
      error: (err) => {
        this.error = 'Ação não encontrada ou erro no servidor.';
        this.isLoadingStock = false;
      }
    });
  }

  askAi() {
    if (!this.ticker) return;
    this.isLoadingAi = true;
    this.aiService.getAdvice(this.ticker).subscribe({
      next: (resp) => {
        this.aiAdvice = resp.content;
        this.isLoadingAi = false;
      },
      error: (err) => {
        this.aiAdvice = 'Erro ao consultar a IA.';
        this.isLoadingAi = false;
      }
    });
  }

  clearAi() {
    this.aiAdvice = '';
  }

  logout() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}