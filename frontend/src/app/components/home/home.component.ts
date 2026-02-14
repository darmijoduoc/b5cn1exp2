import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { User } from 'src/app/models/user';
import { SessionService } from 'src/app/services/session.service';
import { Router, RouterModule } from '@angular/router';
import { Session } from 'src/app/models/session';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [ CommonModule, RouterModule ],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  loggedIn = false;
  session: Session | null = null;

  constructor(
    private sessionService: SessionService,
    private router: Router
  ) { }

  ngOnInit() {
    // Verificar si el usuario está logueado
    this.checkUserStatus();
  }

  checkUserStatus() {
    // Obtener el estado de login de forma simple
    this.sessionService.isLoggedIn().subscribe(isLoggedIn => {
      this.loggedIn = isLoggedIn;
      console.log('Usuario logueado:', isLoggedIn); // Para debuggear
      
      // Si está logueado, obtener información del usuario
      if (isLoggedIn) {
        this.sessionService.getSession().subscribe(session => {
          this.session = session;
          console.log('Datos de la sesión:', session); // Para debuggear
          console.log('displayName:', this.session?.displayName); // Para debuggear específico
        });
      } else {
        this.session = null;
      }
    });
  }

  // Método para obtener el nombre que se mostrará

  logout() {
    this.sessionService.clearSession();
    // this.loggedIn = false;
    // this.user = null;
    this.router.navigate(['/']);
  }
}
