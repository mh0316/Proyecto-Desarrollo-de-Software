import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';
import { ModalConfig } from '../components/modal/modal.component';

@Injectable({
    providedIn: 'root'
})
export class ModalService {
    private modalConfigSubject = new BehaviorSubject<ModalConfig | null>(null);
    public modalConfig$ = this.modalConfigSubject.asObservable();

    private resolveFunction: ((value: any) => void) | null = null;

    /**
     * Show an alert modal
     */
    showAlert(title: string, message: string, type: 'info' | 'success' | 'error' = 'info'): Promise<void> {
        return new Promise((resolve) => {
            this.modalConfigSubject.next({
                title,
                message,
                type,
                confirmText: 'Aceptar'
            });

            this.resolveFunction = () => {
                this.close();
                resolve();
            };
        });
    }

    /**
     * Show a confirm modal
     */
    showConfirm(title: string, message: string): Promise<boolean> {
        return new Promise((resolve) => {
            this.modalConfigSubject.next({
                title,
                message,
                type: 'confirm',
                confirmText: 'Confirmar',
                cancelText: 'Cancelar'
            });

            this.resolveFunction = (value: boolean) => {
                this.close();
                resolve(value);
            };
        });
    }

    /**
     * Show an input modal
     */
    showInput(title: string, message: string, placeholder: string = ''): Promise<string | null> {
        return new Promise((resolve) => {
            this.modalConfigSubject.next({
                title,
                message,
                type: 'input',
                confirmText: 'Aceptar',
                cancelText: 'Cancelar',
                inputPlaceholder: placeholder
            });

            this.resolveFunction = (value: string | boolean) => {
                this.close();
                if (typeof value === 'string') {
                    resolve(value);
                } else if (value === false) {
                    resolve(null);
                } else {
                    resolve(null);
                }
            };
        });
    }

    /**
     * Confirm action
     */
    confirm(value?: string): void {
        if (this.resolveFunction) {
            this.resolveFunction(value !== undefined ? value : true);
        }
    }

    /**
     * Cancel action
     */
    cancel(): void {
        if (this.resolveFunction) {
            this.resolveFunction(false);
        }
    }

    /**
     * Close modal
     */
    private close(): void {
        this.modalConfigSubject.next(null);
        this.resolveFunction = null;
    }
}
