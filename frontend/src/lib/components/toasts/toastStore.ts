import { writable } from 'svelte/store';

type ToastType = 'info' | 'success' | 'error' | 'warning';
type ToastOptions = {
    type: ToastType;
    duration: number;
    action?: { label: string; callback: () => void } | null;
    canClose: boolean;
};
type Toast = {
    id: number;
    message: string;
    options: ToastOptions;
};

function createToastStore() {
    const { subscribe, update } = writable<Toast[]>([]);
    let toastId = 0;

    return {
        subscribe,
        add: (message: string, options: Partial<ToastOptions> = {}) => {
            const id = toastId++;
            const toast: Toast = {
                id,
                message,
                options: {
                    type: options.type || 'info',
                    duration: options.duration ?? 3000,
                    action: options.action || null,
                    canClose: options.canClose ?? true,
                }
            };

            update(toasts => [...toasts, toast]);

            if (toast.options.duration > 0) {
                setTimeout(() => toastStore.remove(id), toast.options.duration);
            }

            return id;
        },
        remove: (id: number) => {
            update(toasts => toasts.filter(t => t.id !== id));
        },
    };
}

export const toastStore = createToastStore();
export type { Toast, ToastOptions, ToastType };