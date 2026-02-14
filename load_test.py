#!/usr/bin/env python3
"""
Script de Carga para Sistema de Monitoreo de Transporte
Envía datos aleatorios a los productores GPS y Schedule
"""

import random
import string
import time
from datetime import datetime, timedelta
from typing import Dict, List

import requests

# Configuración
PRODUCER_GPS_URL = "http://54.221.95.112:8082/api/producer/gps/location"
PRODUCER_SCHEDULE_URL = "http://54.221.95.112:8083/api/producer/schedule/update"

GPS_CALLS = 100
SCHEDULE_CALLS = 20

# Datos base para generación aleatoria
VEHICLE_IDS = [f"BUS-{i:03d}" for i in range(1, 51)]  # BUS-001 a BUS-050
ROUTE_IDS = [f"ROUTE-{i:03d}" for i in range(101, 121)]  # ROUTE-101 a ROUTE-120

# Coordenadas de Santiago, Chile (área metropolitana)
SANTIAGO_LAT_MIN = -33.65
SANTIAGO_LAT_MAX = -33.35
SANTIAGO_LON_MIN = -70.80
SANTIAGO_LON_MAX = -70.50

SCHEDULE_TYPES = ["ARRIVAL", "DEPARTURE", "ROUTE_CHANGE"]
SCHEDULE_STATUSES = ["ON_TIME", "DELAYED", "CANCELLED", "EARLY"]


# Colores ANSI
class Colors:
    GREEN = "\033[92m"
    YELLOW = "\033[93m"
    RED = "\033[91m"
    BLUE = "\033[94m"
    CYAN = "\033[96m"
    RESET = "\033[0m"
    BOLD = "\033[1m"


def print_header(text: str):
    """Imprime un encabezado con formato"""
    print(f"\n{Colors.BLUE}{Colors.BOLD}{'=' * 60}{Colors.RESET}")
    print(f"{Colors.BLUE}{Colors.BOLD}{text:^60}{Colors.RESET}")
    print(f"{Colors.BLUE}{Colors.BOLD}{'=' * 60}{Colors.RESET}\n")


def print_success(text: str):
    """Imprime mensaje de éxito"""
    print(f"{Colors.GREEN}✓{Colors.RESET} {text}")


def print_error(text: str):
    """Imprime mensaje de error"""
    print(f"{Colors.RED}✗{Colors.RESET} {text}")


def print_info(text: str):
    """Imprime mensaje informativo"""
    print(f"{Colors.CYAN}ℹ{Colors.RESET} {text}")


def generate_gps_location() -> Dict:
    """Genera datos aleatorios para ubicación GPS"""
    vehicle_id = random.choice(VEHICLE_IDS)
    latitude = round(random.uniform(SANTIAGO_LAT_MIN, SANTIAGO_LAT_MAX), 6)
    longitude = round(random.uniform(SANTIAGO_LON_MIN, SANTIAGO_LON_MAX), 6)

    # Timestamp reciente (últimas 2 horas)
    minutes_ago = random.randint(0, 120)
    timestamp = datetime.now() - timedelta(minutes=minutes_ago)

    # Speed: 0-80 km/h (distribución realista)
    speed = round(random.triangular(0, 80, 35), 1)

    # Route: 70% tiene ruta asignada
    route = random.choice(ROUTE_IDS) if random.random() < 0.7 else None

    data = {
        "vehicleId": vehicle_id,
        "latitude": latitude,
        "longitude": longitude,
        "timestamp": timestamp.strftime("%Y-%m-%dT%H:%M:%S"),
        "speed": speed,
    }

    if route:
        data["route"] = route

    return data


def generate_schedule_update() -> Dict:
    """Genera datos aleatorios para actualización de horario"""
    # Update ID tipo ULID simplificado
    update_id = "".join(random.choices(string.ascii_uppercase + string.digits, k=16))

    vehicle_id = random.choice(VEHICLE_IDS)
    route_id = random.choice(ROUTE_IDS)
    schedule_type = random.choice(SCHEDULE_TYPES)

    # Scheduled time: próximas 4 horas
    minutes_ahead = random.randint(0, 240)
    scheduled_time = datetime.now() + timedelta(minutes=minutes_ahead)

    # Actual time: puede ser antes, después o igual
    status = random.choice(SCHEDULE_STATUSES)
    if status == "ON_TIME":
        time_diff = random.randint(-2, 2)
    elif status == "DELAYED":
        time_diff = random.randint(5, 30)
    elif status == "EARLY":
        time_diff = random.randint(-30, -5)
    else:  # CANCELLED
        time_diff = 0

    actual_time = scheduled_time + timedelta(minutes=time_diff)

    data = {
        "updateId": update_id,
        "vehicleId": vehicle_id,
        "routeId": route_id,
        "scheduleType": schedule_type,
        "scheduledTime": scheduled_time.strftime("%Y-%m-%dT%H:%M:%S"),
        "actualTime": actual_time.strftime("%Y-%m-%dT%H:%M:%S"),
        "status": status,
        "timestamp": datetime.now().strftime("%Y-%m-%dT%H:%M:%S"),
    }

    return data


def send_gps_location(data: Dict) -> bool:
    """Envía una ubicación GPS al producer"""
    try:
        response = requests.post(
            PRODUCER_GPS_URL,
            json=data,
            headers={"Content-Type": "application/json"},
            timeout=5,
        )
        return response.status_code == 200
    except requests.exceptions.RequestException as e:
        return False


def send_schedule_update(data: Dict) -> bool:
    """Envía una actualización de horario al producer"""
    try:
        response = requests.post(
            PRODUCER_SCHEDULE_URL,
            json=data,
            headers={"Content-Type": "application/json"},
            timeout=5,
        )
        return response.status_code == 200
    except requests.exceptions.RequestException as e:
        return False


def progress_bar(current: int, total: int, width: int = 40):
    """Muestra una barra de progreso"""
    percent = current / total
    filled = int(width * percent)
    bar = "█" * filled + "░" * (width - filled)
    print(
        f"\r{Colors.CYAN}[{bar}]{Colors.RESET} {current}/{total} ({percent * 100:.1f}%)",
        end="",
        flush=True,
    )


def send_gps_batch():
    """Envía lote de ubicaciones GPS"""
    print_header(f"Enviando {GPS_CALLS} Ubicaciones GPS")

    success_count = 0
    fail_count = 0
    start_time = time.time()

    for i in range(GPS_CALLS):
        data = generate_gps_location()

        if send_gps_location(data):
            success_count += 1
        else:
            fail_count += 1

        progress_bar(i + 1, GPS_CALLS)

        # Pequeña pausa para no saturar (10ms)
        time.sleep(0.01)

    elapsed_time = time.time() - start_time

    print()  # Nueva línea después de la barra de progreso
    print()
    print_info(f"Tiempo total: {elapsed_time:.2f} segundos")
    print_info(f"Promedio: {elapsed_time / GPS_CALLS * 1000:.2f} ms por llamada")
    print_success(f"Exitosas: {success_count}")
    if fail_count > 0:
        print_error(f"Fallidas: {fail_count}")
    print()


def send_schedule_batch():
    """Envía lote de actualizaciones de horario"""
    print_header(f"Enviando {SCHEDULE_CALLS} Actualizaciones de Horario")

    success_count = 0
    fail_count = 0
    start_time = time.time()

    for i in range(SCHEDULE_CALLS):
        data = generate_schedule_update()

        if send_schedule_update(data):
            success_count += 1
        else:
            fail_count += 1

        progress_bar(i + 1, SCHEDULE_CALLS)

        # Pequeña pausa para no saturar (10ms)
        time.sleep(0.01)

    elapsed_time = time.time() - start_time

    print()  # Nueva línea después de la barra de progreso
    print()
    print_info(f"Tiempo total: {elapsed_time:.2f} segundos")
    print_info(f"Promedio: {elapsed_time / SCHEDULE_CALLS * 1000:.2f} ms por llamada")
    print_success(f"Exitosas: {success_count}")
    if fail_count > 0:
        print_error(f"Fallidas: {fail_count}")
    print()


def check_services():
    """Verifica que los servicios estén disponibles"""
    print_header("Verificando Servicios")

    gps_ok = False
    schedule_ok = False

    # Verificar Producer GPS
    try:
        response = requests.get(
            "http://localhost:8082/api/producer/gps/health", timeout=3
        )
        if response.status_code == 200:
            print_success("Producer GPS: Disponible")
            gps_ok = True
        else:
            print_error(f"Producer GPS: Error {response.status_code}")
    except requests.exceptions.RequestException:
        print_error("Producer GPS: No disponible")

    # Verificar Producer Schedule
    try:
        response = requests.get(
            "http://localhost:8083/api/producer/schedule/health", timeout=3
        )
        if response.status_code == 200:
            print_success("Producer Schedule: Disponible")
            schedule_ok = True
        else:
            print_error(f"Producer Schedule: Error {response.status_code}")
    except requests.exceptions.RequestException:
        print_error("Producer Schedule: No disponible")

    print()

    if not gps_ok and not schedule_ok:
        print_error("Ningún servicio está disponible. Abortando.")
        return False

    if not gps_ok:
        print_error("Producer GPS no disponible. Solo se enviarán datos a Schedule.")

    if not schedule_ok:
        print_error("Producer Schedule no disponible. Solo se enviarán datos a GPS.")

    return gps_ok, schedule_ok


def print_summary():
    """Imprime resumen final"""
    print_header("Resumen de Ejecución")

    print(f"{Colors.CYAN}Total de llamadas:{Colors.RESET} {GPS_CALLS + SCHEDULE_CALLS}")
    print(f"{Colors.CYAN}Ubicaciones GPS:{Colors.RESET} {GPS_CALLS}")
    print(f"{Colors.CYAN}Actualizaciones Schedule:{Colors.RESET} {SCHEDULE_CALLS}")
    print()
    print_info("Verificar logs de los servicios:")
    print(f"  {Colors.YELLOW}docker logs -f consumer-location{Colors.RESET}")
    print(f"  {Colors.YELLOW}docker logs -f consumer-schedule{Colors.RESET}")
    print()
    print_info("Verificar RabbitMQ Management UI:")
    print(f"  {Colors.YELLOW}http://localhost:15672{Colors.RESET} (admin/admin123)")
    print()


def main():
    """Función principal"""
    print()
    print(
        f"{Colors.BOLD}{Colors.BLUE}╔══════════════════════════════════════════════════════════╗{Colors.RESET}"
    )
    print(
        f"{Colors.BOLD}{Colors.BLUE}║  Script de Carga - Sistema de Monitoreo de Transporte  ║{Colors.RESET}"
    )
    print(
        f"{Colors.BOLD}{Colors.BLUE}╚══════════════════════════════════════════════════════════╝{Colors.RESET}"
    )

    # Verificar servicios
    services = check_services()
    if not services:
        return 1

    gps_ok, schedule_ok = services

    # Enviar datos
    try:
        if gps_ok:
            send_gps_batch()

        if schedule_ok:
            send_schedule_batch()

        print_summary()

        return 0

    except KeyboardInterrupt:
        print()
        print()
        print_error("Ejecución interrumpida por el usuario")
        return 1


if __name__ == "__main__":
    exit(main())
