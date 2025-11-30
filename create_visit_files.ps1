# create_visit_files.ps1
# Chạy từ thư mục gốc project patient_portal

# Lấy đường dẫn gốc của script (chính là thư mục project nếu anh để ps1 ở đó)
$root = Split-Path -Parent $MyInvocation.MyCommand.Path
$base = Join-Path $root "src\main\java\com\patient_porta"

# Các thư mục con theo package
$dirs = @{
    "dto"        = @("VisitSummaryDTO.java", "VisitDetailDTO.java", "VisitDocumentDTO.java")
    "repository" = @("AppointmentRepository.java", "DocumentRepository.java")
    "service"    = @("VisitService.java")
    "controller" = @("VisitController.java")
    "entity"     = @(
        "Appointment.java",
        "DoctorProfile.java",
        "MedicalService.java",
        "Document.java"
    )
}

foreach ($sub in $dirs.Keys) {
    $dirPath = Join-Path $base $sub

    # Tạo thư mục nếu chưa có
    if (-not (Test-Path $dirPath)) {
        New-Item -ItemType Directory -Path $dirPath -Force | Out-Null
    }

    # Tạo từng file rỗng
    foreach ($file in $dirs[$sub]) {
        $filePath = Join-Path $dirPath $file
        if (-not (Test-Path $filePath)) {
            New-Item -ItemType File -Path $filePath -Force | Out-Null
            Write-Host "Created: $filePath"
        } else {
            Write-Host "Exists:  $filePath"
        }
    }
}
