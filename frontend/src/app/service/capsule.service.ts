import {inject, Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Capsule} from '../modele/capsule';
import {CapsuleResume} from '../modele/capsuleResume';
import {CapsuleFormValue} from '../modele/capsuleFormValue';
import {RequestStatus} from '../modele/requestStatus';
import {catchError, Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CapsuleService {
  private http = inject(HttpClient);

  private readonly _capsuleResumes = signal<CapsuleResume[]>([]);
  private readonly _status = signal<RequestStatus>('none');

  readonly capsuleResumes = this._capsuleResumes.asReadonly();
  readonly status = this._status.asReadonly();
  readonly uri :string = 'http://127.0.0.1:9005/api/capsules';

  loadCapsules() {
    // return this.http.get<Capsule[]>('http://127.0.0.1:9005/api/capsules');
    return this.http.get<CapsuleResume[]>(this.uri).subscribe({
      next: (data) => {
        this._capsuleResumes.set(data);
      },
      error: (error) => {
        console.log('Erreur lors du chargement', error);
        this._status.set('loading_error')
        throw error;
      }
    });
  }

  getCapsuleById(id: string) :Observable<Capsule> {
    return this.http.get<Capsule>(`${this.uri}/${id}`).pipe(
      catchError(error => {
        console.log('Erreur lors du chargement de la capsule', error);
        throw error;
      })
    );
  }

  createCapsule(capsuleValue: CapsuleFormValue) {
    return this.http.post<CapsuleResume>(this.uri, capsuleValue).subscribe({
      next: (data) => {
        this._status.set('success');
        this.loadCapsules();
      },
      error: (error) => {
        console.error('Erreur lors de la création', error);
        this._status.set('error');
        throw error;
      }
    });
  }
}
