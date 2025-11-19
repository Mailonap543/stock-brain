import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.html',
  styleUrls: ['./login.scss']
})
export class LoginComponent {
  loginForm: FormGroup;
  errorMessage: string = '';
  successMessage: string = '';
  isLoading: boolean = false;

 
  isRegistering: boolean = false; 

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      password: ['', [Validators.required, Validators.minLength(3)]]
    });
  }

 
  submitAction() {
    if (this.loginForm.invalid) return;

    if (this.isRegistering) {
      this.doRegister();
    } else {
      this.doLogin();
    }
  }

  doLogin() {
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.authService.login(this.loginForm.value).subscribe({
      next: () => {
        this.router.navigate(['/dashboard']);
      },
      error: () => {
        this.errorMessage = 'Falha no login. Verifique usuário e senha.';
        this.isLoading = false;
      }
    });
  }

  doRegister() {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.authService.register(this.loginForm.value).subscribe({
      next: () => {
        this.successMessage = 'Conta criada com sucesso! Faça login.';
        this.isRegistering = false; 
        this.isLoading = false;
      },
      error: () => {
        this.errorMessage = 'Erro ao cadastrar. Usuário já existe?';
        this.isLoading = false;
      }
    });
  }

  
  toggleMode() {
    this.isRegistering = !this.isRegistering;
    this.errorMessage = '';
    this.successMessage = '';
    this.loginForm.reset();
  }
}