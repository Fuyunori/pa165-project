import { Component, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthService } from 'src/app/services/auth.service';

@Component({
  selector: 'tc-landing',
  templateUrl: './landing.component.html',
  styleUrls: ['./landing.component.scss'],
})
export class LandingComponent implements OnInit, OnDestroy {
  readonly loginFailed$ = this.auth.loginFailed$;

  readonly loginForm = this.fb.group({
    username: '',
    password: '',
  });

  private readonly unsubscribe$ = new Subject<void>();

  constructor(
    private readonly fb: FormBuilder,
    private readonly auth: AuthService,
  ) {}

  ngOnInit(): void {
    this.auth.loginFailed$
      .pipe(takeUntil(this.unsubscribe$))
      .subscribe(loginFailed => {
        Object.values(this.loginForm.controls).forEach(control => {
          control.setErrors(loginFailed ? { loginFailed } : null);
        });
      });
  }

  ngOnDestroy(): void {
    this.unsubscribe$.next();
    this.unsubscribe$.complete();
  }

  logIn(): void {
    const { username, password } = this.loginForm.value;
    this.auth.logIn(username, password);
  }
}
