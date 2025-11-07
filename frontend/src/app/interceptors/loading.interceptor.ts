import { HttpInterceptorFn } from '@angular/common/http';
import { finalize } from 'rxjs';

let activeRequests = 0;

export const loadingInterceptor: HttpInterceptorFn = (req, next) => {
  activeRequests++;

  return next(req).pipe(
    finalize(() => {
      activeRequests--;
    })
  );
};