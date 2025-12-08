import { Component, EventEmitter, Input, Output, OnChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

export interface ModalConfig {
    title: string;
    message: string;
    type: 'info' | 'success' | 'error' | 'confirm' | 'input';
    confirmText?: string;
    cancelText?: string;
    inputValue?: string;
    inputPlaceholder?: string;
}

@Component({
    selector: 'app-modal',
    standalone: true,
    imports: [CommonModule, FormsModule],
    templateUrl: './modal.component.html',
    styleUrls: ['./modal.component.scss']
})
export class ModalComponent implements OnChanges {
    @Input() config: ModalConfig | null = null;
    @Output() confirmed = new EventEmitter<string | void>();
    @Output() cancelled = new EventEmitter<void>();

    inputValue = '';

    get isVisible(): boolean {
        return this.config !== null;
    }

    ngOnChanges(): void {
        if (this.config?.type === 'input') {
            this.inputValue = this.config.inputValue || '';
        }
    }

    onConfirm(): void {
        if (this.config?.type === 'input') {
            this.confirmed.emit(this.inputValue);
        } else {
            this.confirmed.emit();
        }
    }

    onCancel(): void {
        this.cancelled.emit();
    }

    onBackdropClick(event: MouseEvent): void {
        if (event.target === event.currentTarget) {
            this.onCancel();
        }
    }
}
