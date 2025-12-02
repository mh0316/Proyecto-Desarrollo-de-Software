import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { Router } from '@angular/router';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  const token = authService.getToken();

  // Opcional: limitar solo a requests de tu API si tienes environment.apiUrl
  // import { environment } from '../../environments/environment';
  // const isApiRequest = req.url.startsWith(environment.apiUrl);

  const shouldAttachAuth =
    !!token &&
    !req.url.includes('/login') &&
    !req.url.includes('/registro');
  // && isApiRequest;

  const authReq = shouldAttachAuth
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 || error.status === 403) {
        // Manejo básico: cerrar sesión y redirigir
        authService.logout();
        router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};
