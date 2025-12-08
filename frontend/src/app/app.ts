import { NgModule } from '@angular/core';
import { Component, OnInit } from '@angular/core';
import { RouterOutlet, Router, provideRouter } from '@angular/router';
import { routes } from './app.routes';
import { importProvidersFrom } from '@angular/core';
import { HttpClientModule } from '@angular/common/http';
import { bootstrapApplication } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from './components/header/header.component';
import { SidebarComponent } from './components/sidebar/sidebar.component';
import { AuthService } from './services/auth.service';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, CommonModule, HeaderComponent, SidebarComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss'
})
export class App implements OnInit {
  isAuthenticated = false;
  isLoginOrRegisterPage = false;

  constructor(
    private authService: AuthService,
    private router: Router
  ) { }

  ngOnInit(): void {
    // Subscribe to authentication state
    this.authService.isAuthenticated$.subscribe(
      isAuth => this.isAuthenticated = isAuth
    );

    // Subscribe to route changes to detect login/register pages
    this.router.events.subscribe(() => {
      const currentUrl = this.router.url;
      this.isLoginOrRegisterPage = currentUrl.includes('/login') || currentUrl.includes('/registro');
    });
  }

  get showLayout(): boolean {
    return this.isAuthenticated && !this.isLoginOrRegisterPage;
  }
}

// ✅ Bootstrap con los providers correctos
bootstrapApplication(App, {
  providers: [
    provideRouter(routes),
    importProvidersFrom(HttpClientModule)
  ]
}).catch(err => console.error(err));
