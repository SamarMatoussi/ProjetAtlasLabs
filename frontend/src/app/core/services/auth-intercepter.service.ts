import { HttpEvent, HttpHandler, HttpHeaders, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthenticationResponse } from '../models/authentication-response';

@Injectable({
  providedIn: 'root'
})
export class AuthIntercepterService implements HttpInterceptor{

  constructor() { }
  
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
 
  if(localStorage.getItem("accesstoken")){
 req=req.clone({
  headers:new  HttpHeaders({
    Authorization:"Bearer "+localStorage.getItem("accesstoken") as string
  })
})
  }
return next.handle(req)
  }
}
