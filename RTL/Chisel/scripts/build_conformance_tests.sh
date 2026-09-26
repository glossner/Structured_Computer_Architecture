#!/usr/bin/env bash
# Licensed under the Solderpad Hardware License v 2.1
# See: https://solderpad.org/licenses/SHL-2.1/
#
# Generates raw test binaries (.bin) and reference signatures (.reference.sig)
# for the official RISC-V architectural conformance tests (RV32I and Zmmul).
#
# Requires:
#   - riscv64-unknown-elf-gcc (or riscv32-unknown-elf-gcc)
#   - riscv64-unknown-elf-objcopy
#   - riscv64-unknown-elf-nm
#   - spike (RISC-V ISA simulator)
#
# Usage:
#   ./build_conformance_tests.sh <path_to_riscv_arch_test_suite> <path_to_env> [output_dir]

set -euo pipefail

SCRIPT_DIR=$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)
CHISEL_ROOT=$(dirname "$SCRIPT_DIR")
OUT_DIR="${3:-$CHISEL_ROOT/src/test/resources/conformance}"

SOURCE_DIR="${1:-/workspaces/KryptoNyte/.venv/tools/riscv-conformance/riscv-arch-test/riscv-test-suite}"
ENV_DIR="${2:-$SOURCE_DIR/env}"
LINK_LD="${LINK_LD:-$ENV_DIR/link.ld}"
SPIKE="${SPIKE:-spike}"

mkdir -p "$OUT_DIR"
METADATA="$OUT_DIR/test_metadata.csv"
echo "test_name,tohost,begin_sig,end_sig" > "$METADATA"

I_TESTS=(
  add-01 addi-01 and-01 andi-01 auipc-01 beq-01 bge-01 bgeu-01 blt-01 bltu-01 bne-01 fence-01
  jal-01 jalr-01 lb-align-01 lbu-align-01 lh-align-01 lhu-align-01 lui-01 lw-align-01 or-01 ori-01
  sb-align-01 sh-align-01 sll-01 slli-01 slt-01 slti-01 sltiu-01 sltu-01 sra-01 srai-01 srl-01 srli-01
  sub-01 sw-align-01 xor-01 xori-01
)

for t in "${I_TESTS[@]}"; do
  s_file="$SOURCE_DIR/rv32i_m/I/src/${t}.S"
  if [ ! -f "$s_file" ]; then
    echo "Warning: Missing $s_file, skipping"
    continue
  fi
  echo "Building $t ..."
  tmp_elf=$(mktemp)
  riscv64-unknown-elf-gcc -march=rv32i -mabi=ilp32 -mcmodel=medany -static -nostdlib -nostartfiles -g \
    -T "$LINK_LD" -I "$ENV_DIR" "$s_file" -o "$tmp_elf" -DXLEN=32 -DTEST_CASE_1=True

  "$SPIKE" --isa=rv32i "+signature=$OUT_DIR/${t}.reference.sig" +signature-granularity=4 "$tmp_elf"
  riscv64-unknown-elf-objcopy -O binary "$tmp_elf" "$OUT_DIR/${t}.bin"

  TOHOST=$(riscv64-unknown-elf-nm "$tmp_elf" | awk '$3=="tohost"{print "0x"$1}')
  BEGIN_SIG=$(riscv64-unknown-elf-nm "$tmp_elf" | awk '$3=="begin_signature"{print "0x"$1}')
  END_SIG=$(riscv64-unknown-elf-nm "$tmp_elf" | awk '$3=="end_signature"{print "0x"$1}')

  echo "$t,$TOHOST,$BEGIN_SIG,$END_SIG" >> "$METADATA"
  rm -f "$tmp_elf"
done

M_TESTS=(mul-01 mulh-01 mulhsu-01 mulhu-01)
for t in "${M_TESTS[@]}"; do
  s_file="$SOURCE_DIR/rv32i_m/M/src/${t}.S"
  if [ ! -f "$s_file" ]; then
    echo "Warning: Missing $s_file, skipping"
    continue
  fi
  echo "Building $t ..."
  tmp_elf=$(mktemp)
  riscv64-unknown-elf-gcc -march=rv32im -mabi=ilp32 -mcmodel=medany -static -nostdlib -nostartfiles -g \
    -T "$LINK_LD" -I "$ENV_DIR" "$s_file" -o "$tmp_elf" -DXLEN=32 -DTEST_CASE_1=True

  "$SPIKE" --isa=rv32im "+signature=$OUT_DIR/${t}.reference.sig" +signature-granularity=4 "$tmp_elf"
  riscv64-unknown-elf-objcopy -O binary "$tmp_elf" "$OUT_DIR/${t}.bin"

  TOHOST=$(riscv64-unknown-elf-nm "$tmp_elf" | awk '$3=="tohost"{print "0x"$1}')
  BEGIN_SIG=$(riscv64-unknown-elf-nm "$tmp_elf" | awk '$3=="begin_signature"{print "0x"$1}')
  END_SIG=$(riscv64-unknown-elf-nm "$tmp_elf" | awk '$3=="end_signature"{print "0x"$1}')

  echo "$t,$TOHOST,$BEGIN_SIG,$END_SIG" >> "$METADATA"
  rm -f "$tmp_elf"
done

echo "Done! All conformance tests built to $OUT_DIR."
